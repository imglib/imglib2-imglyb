#!/bin/sh
curl -fsLO https://raw.githubusercontent.com/scijava/scijava-scripts/master/travis-build.sh
sh travis-build.sh $encrypted_cb84f55eba9a_key $encrypted_cb84f55eba9a_iv
