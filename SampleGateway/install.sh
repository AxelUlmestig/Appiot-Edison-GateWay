mvn install:install-file \
        -Dfile=/usr/lib/java/tinyb.jar \
        -DgroupId=tinyb \
        -DartifactId=tinyb \
        -Dversion=1.0 \
        -Dpackaging=jar \
        -DgeneratePom=true
