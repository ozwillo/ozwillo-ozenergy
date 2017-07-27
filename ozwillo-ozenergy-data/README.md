# ozwillo-ozenergy-data

Energy data, models and a tool to inject data from a huge CSV file into the [Ozwillo Datacore](https://github.com/ozwillo/ozwillo-datacore).

## Scripts

The CsvResourceBulkImportService.java in ./src/main/org/ozwillo/ozenergy/data is an unfinished attempt (to be run from your favourite IDE), used to import data data in batches into the datacore.

To launch it, you will need to have installed the Datacore in your local Maven repository (using 'mvn clean install' in its main directory : make sure the host and port configured in ./src/main/resources/oasis-datacore-ozenergy-data.properties and in the Datacore oasis-datacore-ozenergy-data.properties files are the same as your running Datacore instance).

The CsvResourceBulkImportService.java in ./src/main/org/ozwillo/ozenergy/data is an unfinished attempt to transform the JUnit test into a proper Spring Service.

## Data

You will find the data in the ./src/main/resources/energy folder. The model to be used is the most recent one, namely "energy_model_v1.2.ods" at the time of this writing. If you wish to modify the model, open "energy_model_v1.2.ods" in Libreoffice Calc, edit it, then export the different calc sheets you modified as .csv.

Data are available under [CC BY 3.0 AU](https://creativecommons.org/licenses/by/3.0/au/) licence at [http://data.gov.au/dataset/sample-household-electricity-time-of-use-data]
(http://data.gov.au/dataset/sample-household-electricity-time-of-use-data).
