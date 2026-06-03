FROM intersystems/irishealth-community:2025.1

USER root

RUN mkdir -p /opt/irisapp && chown ${ISC_PACKAGE_MGRUSER}:${ISC_PACKAGE_IRISGROUP} /opt/irisapp
WORKDIR /opt/irisapp

ENV PIP_TARGET=${ISC_PACKAGE_INSTALLDIR}/mgr/python

COPY --chown=${ISC_PACKAGE_MGRUSER}:${ISC_PACKAGE_IRISGROUP} iris.script /
COPY --chown=${ISC_PACKAGE_MGRUSER}:${ISC_PACKAGE_IRISGROUP} ./src src
COPY --chown=${ISC_PACKAGE_MGRUSER}:${ISC_PACKAGE_IRISGROUP} ./data fhirdata

USER ${ISC_PACKAGE_MGRUSER}

RUN iris start IRIS \
    && iris session IRIS < /iris.script \
    && iris stop IRIS quietly