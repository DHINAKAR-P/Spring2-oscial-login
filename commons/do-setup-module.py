#! /usr/bin/env python3
from python import *

Utils.do([root_dir, 'git'], 'config', 'core.hooksPath', './commons/git/hooks')
