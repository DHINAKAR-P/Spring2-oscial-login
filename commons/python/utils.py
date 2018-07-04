import os
import ssl
import subprocess
import urllib.error
import urllib.request

import sys
import time


class Utils:
    module_root_dir = f'{os.path.dirname(__file__)}/../..'

    @staticmethod
    def do(command, *args):
        wd = cwd = os.getcwd()
        if type(command) is list:
            wd, command = command
        args = list(filter(None, list(args)))  # remove all falsy elements
        command = [command] + args
        os.chdir(wd)
        print(os.getcwd() + ": " + ' '.join(command))
        shell = os.name == 'nt'
        process = subprocess.Popen(command, stdout=subprocess.PIPE, shell=shell, universal_newlines=True)
        (stdout, stderr) = process.communicate()
        if process.returncode:
            sys.exit(f'___STD_ERR___\n{str(stderr)}\n___STD_OUT___\n{str(stdout)}')
        os.chdir(cwd)
        return stdout

    @staticmethod
    def execute(python_file, args=''):
        wd = cwd = os.getcwd()
        if type(python_file) is list:
            wd, python_file = python_file
        os.chdir(wd)
        os.system(f'{sys.executable} {python_file}.py {args}')
        os.chdir(cwd)

    @staticmethod
    def is_up(endpoint):
        try:
            code = urllib.request.urlopen(endpoint, context=ssl._create_unverified_context()).getcode()
            return True if code == 200 else False
        except Exception as e:
            print(type(e).__name__ + ':', e)
        return False

    @staticmethod
    def up_in_time(endpoint, seconds=180):
        print(f'Checking whether {endpoint} is accessible:')
        for attempt in range(0, seconds):
            print(f'Attempt #{attempt:3d} to establish connection: ', end='')
            if Utils.is_up(endpoint):
                print(f'Connection established successfully. Endpoint {endpoint} is up and running.')
                return True
            time.sleep(1)
        print('Unable to establish connection.')
        return False
