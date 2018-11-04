#!/bin/bash

set -eux

java -cp $(./tools/classpath.sh) \
  jp.vmi.selenium.selenese.utils.CommandDumper --side |
  fgrep -v '[main]' | tee docs/supported-side-commands.txt 
