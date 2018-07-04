import configparser
import os
import re
from pathlib import Path

import sys
import yaml

from .utils import Utils

__all__ = ['MavenUtils']

_pom_properties_file_path = f'{Utils.module_root_dir}/target/module.properties'
_pom_properties_file_updated = False


def _write_or_update_pom_properties_file_if_needed(profile):
    global _pom_properties_file_updated
    if not Path(_pom_properties_file_path).exists() or not _pom_properties_file_updated:
        Utils.do([Utils.module_root_dir, 'mvn'], f'--activate-profiles={profile}',
                 'yaml-properties:write-project-properties')
        _pom_properties_file_updated = True


def _get_pom_properties_as_stream(profile):
    _write_or_update_pom_properties_file_if_needed(profile)
    return open(f'{Utils.module_root_dir}/target/module.properties', 'r')


def _get_pom_properties(profile):
    parser = configparser.RawConfigParser()
    section_name = 'dummy_section'
    parser.read_string(f'[{section_name}]\n' + _get_pom_properties_as_stream(profile).read())
    return dict(parser.items(section_name))


def _convert_to_flat_properties(yml_properties):
    def concat(path, key):
        return path + '.' + key if path else key

    def flatten(data, path, result):
        for key in data:
            value = data[key]
            if type(value) is dict:
                flatten(value, concat(path, key), result)
            else:
                result[concat(path, key)] = value

    flat_properties = {}
    flatten(yml_properties, '', flat_properties)
    return flat_properties


def _get_yml_property_from_file(file_name, key):
    with open(file_name, 'r') as stream:
        yml_properties = yaml.load(stream)
        if yml_properties is None:
            return None
        properties = _convert_to_flat_properties(yml_properties)
        return properties.get(key, None)


def _get_yml_property(key, profile):
    folder = f"{os.path.dirname(__file__)}/../../properties"
    value = _get_yml_property_from_file(f"{folder}/-/{profile}.properties.yml", key)
    return value if value is not None else _get_yml_property_from_file(f"{folder}/default.properties.yml", key)


def _interpolate(value, dictionary):
    return value if not isinstance(value, str) else re.sub("@{([^}]+)}", lambda m: dictionary[m.group(1)], value)


class MavenUtils:
    @staticmethod
    def get(key, profile='dev'):
        yml_value = _get_yml_property(key, profile)
        pom_properties = _get_pom_properties(profile)
        try:
            return _interpolate(yml_value, pom_properties) if yml_value is not None else pom_properties[key]
        except KeyError as keyError:
            sys.exit(f"No such property: '{keyError.args[0]}'.")

    @staticmethod
    def rebuild(profiles='dev'):
        Utils.do([Utils.module_root_dir, 'mvn'], f'--activate-profiles={profiles}', 'clean', 'verify')
