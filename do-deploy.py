#! /usr/bin/env python3
from commons.python import Utils, MavenUtils, DockerUtils

# Read maven properties:
port = MavenUtils.get('port')
debug_port = MavenUtils.get('debug_port')
neo4j_protocol = MavenUtils.get("neo4j.protocol")
neo4j_domain = MavenUtils.get("neo4j.domain")
neo4j_browser_port = MavenUtils.get("neo4j.browser_port")
docker_image_tag = MavenUtils.get('docker.image_tag')
docker_container_name = MavenUtils.get('docker.container_name')
docker_network_name = MavenUtils.get('docker.network_name')

MavenUtils.rebuild()

Utils.execute('do-undeploy')

DockerUtils.create_network_if_not_exists(docker_network_name)

# Build docker image and run it as docker container:
DockerUtils.build_image(docker_image_tag)
if Utils.up_in_time(f'{neo4j_protocol}://{neo4j_domain}:{neo4j_browser_port}'):
    Utils.do(
        'docker', 'run', '--detach', f'--name={docker_container_name}', f'--net={docker_network_name}',
        '--env', 'JAVA_OPTS='
                 f'-agentlib:jdwp=transport=dt_socket,server=y,address={debug_port},suspend=n '
                 f'-agentpath:/libjrebel64.so -Drebel.remoting_plugin=true',
        '--publish', f'{port}:{port}',
        '--publish', f'{debug_port}:{debug_port}',
        docker_image_tag
    )

DockerUtils.clean_system()
