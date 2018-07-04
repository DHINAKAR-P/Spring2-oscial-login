#! /usr/bin/env python3
from commons.python import MavenUtils, DockerUtils

# Read maven properties:
port = MavenUtils.get('port')
debug_port = MavenUtils.get('debug_port')
docker_container_name = MavenUtils.get('docker.container_name')

# Stop and remove any containers that clashes with module container:
containers = DockerUtils.search_containers(f"publish={port}", f'publish={debug_port}', f'name={docker_container_name}')
DockerUtils.stop_and_remove_containers(containers)

DockerUtils.clean_system()
