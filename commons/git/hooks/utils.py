#! /usr/bin/env python3
import datetime
import time
import subprocess as sp

from ruamel import yaml
from ruamel.yaml.util import load_yaml_guess_indent

__all__ = ["GitHooksUtils"]

module_metadata = 'properties/commit.yml'


def spout_to_str(subprocess):
    """Converts output of a subprocess to string."""
    return subprocess.stdout.decode('utf-8').strip()


def spout_to_int(subprocess):
    return int(spout_to_str(subprocess))


class GitHooksUtils:
    @staticmethod
    def timestamp():
        return datetime.datetime.utcfromtimestamp(int(time.time())).isoformat() + 'Z'

    @staticmethod
    def get_branch_of_head():
        return spout_to_str(sp.run(['git', 'rev-parse', '--abbrev-ref', 'HEAD'], stdout=sp.PIPE))

    @staticmethod
    def get_number_of_commits_before_head():
        first_commit = spout_to_str(sp.run(['git', 'rev-list', '--max-parents=0', 'HEAD'], stdout=sp.PIPE))
        commits_from_2nd_to_head = sp.run(['git', 'log', '--oneline', f'{first_commit}..HEAD'], stdout=sp.PIPE).stdout
        return int(spout_to_str(sp.run(['wc', '-l'], stdout=sp.PIPE, input=commits_from_2nd_to_head)))

    @staticmethod
    def get_revision_of_head():  # revision is a commit's serial number
        return GitHooksUtils.get_number_of_commits_before_head() + 1  # revisions are counted from 1 (not 0)

    @staticmethod
    def update_metadata(timestamp, branch, revision):
        properties, indent, block_seq_indent = load_yaml_guess_indent(open(module_metadata))
        properties['commit']['timestamp'] = timestamp
        properties['commit']['branch'] = branch
        properties['commit']['revision'] = revision
        yaml.round_trip_dump(properties, open(module_metadata, 'w'), indent=indent, block_seq_indent=block_seq_indent)
        sp.run(['git', 'add', module_metadata])

    @staticmethod
    def rewrite_commit():
        sp.run(['git', 'commit', '--amend', '--no-verify', '--no-edit'])

    @staticmethod
    def reindex_metadata():
        sp.run(['git', 'update-index', '--', module_metadata])
