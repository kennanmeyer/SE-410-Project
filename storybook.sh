#!/bin/sh

echo "starting Storybook ..."
java -Dfile.encoding=UTF-8 -XX:MaxPermSize=256m -Xmx300m -jar lib/storybook.jar $*
echo "done."
