IMGLYB_SHARE=$PREFIX/share/imglyb
mkdir -p $IMGLYB_SHARE
cp $PREFIX/pyjnius-bdv.py $IMGLYB_SHARE
cp $PREFIX/butterfly.jpg $IMGLYB_SHARE

LOCAL_REPO=$PREFIX/.m2
mkdir -p $LOCAL_REPO
mvn -Dmaven.repo.local=$LOCAL_REPO clean install


# ensure that IMGLYB_JAR is set correctly
mkdir -p $PREFIX/etc/conda/activate.d
echo 'export IMGLYB_JAR_BACKUP=$IMGLYB_JAR' > "$PREFIX/etc/conda/activate.d/imglyb_jar.sh"
echo 'export IMGLIB_JAR=$CONDA_PREFIX/.m2/repository/net/imglib2/imglib2-python-fat-jar/0.0.1-SNAPSHOT/imglib2-python-fat-jar-0.0.1-SNAPSHOT-jar-with-dependencies.jar' >> "$PREFIX/etc/conda/activate.d/imglyb_jar.sh"
mkdir -p $PREFIX/etc/conda/deactivate.d
echo 'export JAVA_HOME=$JAVA_HOME_CONDA_BACKUP' > "$PREFIX/etc/conda/deactivate.d/imglyb_jar.sh"
