#!/usr/bin/env python3
import click
import logging
import requests
from rich.logging import RichHandler

FORMAT = "%(message)s"
logging.basicConfig(
    level="NOTSET", format=FORMAT, datefmt="[%X]", handlers=[RichHandler()]
)

requests_log = logging.getLogger("urllib3")
requests_log.setLevel(logging.DEBUG)
requests_log.propagate = True
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
requests_log.addHandler(ch)


@click.command()
@click.option("-c", "--count", default=10, help="Number of iterations.")
@click.option("-d", "--default-client", is_flag=True, help="Use the default WebClient.")
def trigger(count, default_client):
    """Simple program that triggers a web service."""
    params = {"defaultClient": True if default_client else False}
    for i in range(0, count):
        requests.get("http://localhost:6110/trigger", params=params)


if __name__ == "__main__":
    trigger()
