#! /usr/bin/env python3
from commons.python import Utils

Utils.do('mvn', 'antrun:run@ktlint-apply-to-idea-project')
