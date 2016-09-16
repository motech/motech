#!/usr/bin/env bash

if [ "$TRAVIS_EVENT_TYPE" = "cron" ]; then
    curl -X POST http://readthedocs.org/build/motech-project
    curl -X POST http://readthedocs.org/build/motech-modules
fi
