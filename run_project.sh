#!/usr/bin/env bash

./gradlew clean build installBootDist

ret_code=$?
if [ ${ret_code} != 0 ]; then
    printf "Error: [%d] when executing clean build installBootDist. Aborting\n" ${ret_code}
    exit ${ret_code}
fi

sh ./build/install/java-windowed-statistics-boot/bin/java-windowed-statistics