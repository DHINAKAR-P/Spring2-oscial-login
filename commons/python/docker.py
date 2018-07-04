from .utils import Utils

__all__ = ['DockerUtils']


class DockerUtils:
    @staticmethod
    def create_network_if_not_exists(network_name):
        network_names = Utils.do('docker', 'network', 'ls', '--format={{.Name}}').splitlines()
        if network_name not in network_names:
            Utils.do('docker', 'network', 'create', '--driver', 'bridge', network_name)

    @staticmethod
    def build_image(tag):
        Utils.do([f'{Utils.module_root_dir}/target/docker', 'docker'], 'build', f'--tag={tag}', '.')

    @staticmethod
    def search_containers(*conditions):
        containers = []
        for condition in conditions:
            containers += Utils.do('docker', 'ps', '--all', '--quiet', '--filter', f'{condition}').splitlines()
        return set(containers)

    @staticmethod
    def stop_and_remove_containers(containers):
        for container in containers:
            Utils.do('docker', 'stop', container)
            Utils.do('docker', 'rm', container)

    @staticmethod
    def clean_system():
        Utils.do('docker', 'system', 'prune', '--force')
