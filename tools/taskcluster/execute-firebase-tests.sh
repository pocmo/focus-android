#!/usr/bin/env bash
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.

# This script uses the flank tool (https://github.com/TestArmada/flank)
# to shard UI test execution on Google Firebase so tests can run in parallel.  
#
# NOTE: 
#   Google Firebase does not currently allow renaming nor grouping of test
#   jobs.  recommendation: look at test failure summary in taskcluster
#   (not on Firebase dashboard)


# From now on disable exiting on error. If the tests fail we want to continue
# and try to download the artifacts. We will exit with the actual error code later.
set +e

URL_FLANK_BIN="https://github.com/Flank/flank/releases/download/v21.03.1/flank.jar"
JAVA_BIN="/usr/bin/java"
WORKDIR="/opt/focus-android"
PATH_TEST="$WORKDIR/app/src/androidTest/java/org/mozilla/focus/activity"
PATH_TOOLS="$WORKDIR/tools/taskcluster"
PACKAGE="org.mozilla.focus.activity"
FLANK_BIN="$PATH_TOOLS/flank.jar"
FLANK_CONF="$PATH_TOOLS/flank.yml"

echo "home: $HOME"
export GOOGLE_APPLICATION_CREDENTIALS="$WORKDIR/.firebase_token.json"

FLANK_CONF_TEMPLATE="$PATH_TOOLS/flank-conf.yml"

rm  -f TEST_TARGETS 
rm  -f $FLANK_BIN

echo
echo "---------------------------------------"
echo "FLANK FILES"
echo "---------------------------------------"
echo "FLANK_CONF_TEMPLATE: $FLANK_CONF_TEMPLATE"
echo "FLANK_CONF: $FLANK_CONF"
echo "FLANK_BIN: $FLANK_BIN"
echo

curl --location --retry 5 --output $FLANK_BIN $URL_FLANK_BIN

echo
echo "---------------------------------------"
echo "FLANK VERSION"
echo "---------------------------------------"
chmod +x $FLANK_BIN
$JAVA_BIN -jar $FLANK_BIN -v
echo
echo


# create TEST_TARGETS 
# (aka: get list of all tests in androidTest)
file_list=$(ls $PATH_TEST | xargs -n 1 basename)
array=( $file_list )

test_targets=""
for i in "${array[@]}"; do
    test_list=`cat $PATH_TEST/$i | grep -hr "public void" | grep 'Test\|test'| sed 's/ \{1,\}/ /g'  | cut -d" " -f 4 | sed 's/()//g'`
    array2=( $test_list)
    test_file=$(basename $i .java)
    
    for j in "${array2[@]}"; do
        echo "    - class $PACKAGE.$test_file#$j" >> TEST_TARGETS
    done
done

test_targets=`cat TEST_TARGETS`
test_targets=$(echo "${test_targets}" | sed '$!s~$~\\~g')

# create flank.yml config file 
sed "s/TEST_TARGETS/${test_targets}/g" $FLANK_CONF_TEMPLATE > $FLANK_CONF
rm -f TEST_TARGETS
$JAVA_BIN -jar $FLANK_BIN android run --config=$FLANK_CONF
exitcode=$?

cp -r "$WORKDIR/results" "$WORKDIR/test_artifacts"

# Now exit the script with the exit code from the test run. (Only 0 if all test executions passed)
exit $exitcode
