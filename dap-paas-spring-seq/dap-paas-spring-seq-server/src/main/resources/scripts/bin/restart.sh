#!/bin/bash
current_dir="$( cd "$( dirname "$0"  )" && pwd  )"
sh ${current_dir}/stop.sh
sh ${current_dir}/start.sh
