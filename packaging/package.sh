#windows or linux
if [ "$1" = "-windows" ] ; then
    echo "Got '-windows' flag. Running Windows version"
    LINUX=false
else
    echo "No '-windows' flag. Running linux version"
    LINUX=true
fi

#read the folder this script should be in (should be the packaging folder inside the ATCS source)
PACKAGING_DIR=$(dirname "$(readlink -f "$0" || greadlink -f "$0" || stat -f "$0")")
ATCS_SOURCE_DIR=$(dirname "${PACKAGING_DIR}")
TEMP_DIR=${PACKAGING_DIR}/tmp
echo "Packaging dir: ${PACKAGING_DIR}"
echo "ATCS_SOURCE_DIR: ${ATCS_SOURCE_DIR}"
#ATContentStudio
JAR_LOCATION="${PACKAGING_DIR}/common/ATCS.jar"
MANIFEST_LOCATION=${PACKAGING_DIR}/Manifest.txt
echo ""

echo "Getting version"
VERSION=$(cat "${PACKAGING_DIR}"/ATCS_latest)
echo "Got version ${VERSION}"

echo "Removing tmp folder"
rm -rf "${PACKAGING_DIR}"/tmp/
echo "recreating tmp folder"
mkdir "${PACKAGING_DIR}"/tmp/
#ATCS_SOURCE_DIR="${PACKAGING_DIR}/.."
echo ""
#copy manifest to temp folder for editing
cp "${MANIFEST_LOCATION}" "${TEMP_DIR}"
MANIFEST_LOCATION=${TEMP_DIR}/Manifest.txt

#copy lib files to packaged folder?
echo 'copying lib files'
mkdir -p "${PACKAGING_DIR}"/common/lib/
cp "${ATCS_SOURCE_DIR}"/lib/* "${PACKAGING_DIR}"/common/lib/

cd "$ATCS_SOURCE_DIR" || exit
#set ClassPath variable to use in the building etc.
echo 'setting class path'
#linux needs a : as separator while windows needs ;
if [ "$LINUX" = true ] ; then
    CP="lib/*:src:hacked-libtiled:siphash-zackehh/src/main/java"
else
    CP="lib/*;src;hacked-libtiled;siphash-zackehh/src/main/java"
fi
echo "ClassPath: "
echo "${CP}"
echo ""

#set build the classes
echo 'building java classes'
#javac -cp $CP *.java
javac -cp "$CP" "${ATCS_SOURCE_DIR}"/src/com/gpl/rpg/atcontentstudio/*.java -d "${TEMP_DIR}"
echo javac -cp "$CP" "${ATCS_SOURCE_DIR}"/src/com/gpl/rpg/atcontentstudio/*.java -d "${TEMP_DIR}"
echo ""
LIB_PATHS=$(find lib -name '*.jar' | paste -sd' ')
echo "LIB_PATHS: ${LIB_PATHS}"
# add all lib files to the class path in the temp Manifest
echo "Class-Path: . lib/* ${LIB_PATHS}" >>"${MANIFEST_LOCATION}"

echo ""
echo "creating jar at location: ${JAR_LOCATION}"

# create the jar file
# the command with those parameters requires this format:
# jar mfc <Manifest file> <Jar file (target location)> <all things to add to the jar file>
# the things to add always use the whole relative path from the current dir,
# so when that is not wanted, the -C <location> thing will change to that dir
jar mfc "${MANIFEST_LOCATION}" "${JAR_LOCATION}" -C "${PACKAGING_DIR}"/tmp/ com/gpl/rpg/atcontentstudio/ -C res . -C "${ATCS_SOURCE_DIR}"/src .

echo ''
echo "Done creating jar"

if [ "$LINUX" = true ] ; then
    cd "${PACKAGING_DIR}" || exit
    echo "Creating archive"
    tar caf "ATCS_${VERSION}.tar.gz" "common"
    echo "Created archive at ${PACKAGING_DIR}/ATCS_${VERSION}.tar.gz"
else
	echo "Can't create zip files on windows yet. Please pack the content of the '${PACKAGING_DIR}/common/' folder yourself"
fi
