# pylint: disable=missing-docstring
import pathlib

import yaml

from src.base.python import gentemplate
from src.base.python import util

THIS_DIR = pathlib.Path(__file__).resolve().parent
CONFIG_DIR = util.get_workspace() / 'config.d'


@gentemplate.template(THIS_DIR / 'application.conf.j2')
def generate_process_manager_config():
    path = CONFIG_DIR / 'deploy_config.yaml'
    with open(str(path), 'r') as f:  # pylint: disable = invalid-name
        data = yaml.load(f.read())
        return data

