# event-impact
Supervised Analaysis and Prediction of Impact of Planned Special Events on Urban Traffic

## Prerequisites

Java 11

Maven

You can then create a maven projet using the provided pom.xml file.

## Runing
You can run the Application class and provide one or more configuration files as an argument.

## Configuration Parameters
The following configuration parameters can be set in the configuration file. Parameters are set in the form of key = value, one parameter per line.

dbHost  -  IP of the Postgre database

dbUser  -   Name of the database use

dbPassword -    Password of the database user

dbName -    Name of the database

dbMaxConnections -  Number of maximum allowed simultaneous connections

dbSchema - Target schema in the database

spatialImpact - true/false Activate or deactivate the spatial impact calculation

temporalImpact - true/false Activate or deactivate the temporal impact calculation

typicallyAffectedSubgraph - true/false Activate or deactivate the typically affected subgraph calculation

streetGraphTable - Table in which a pgrouting graph of streets is stored

sourceColumn - Column of source ids in streetGraphTable

targetColumn -  Column of taget ids in streetGraphTable

idColumn - Column of street ids in streetGraphTable

geometryColumn - Column of geometry in streetGraphTable

trafficTable - Table that holds traffic information

lossColumn - Traffic speed loss in trafficTable

outlierColumn - Outlier flag column in trafficTable

timeColumn - Time column in trafficTable

th_affected - Threshold when a unit is considered to be affected

th_typicall - yAffected Threshold for edges of the typically affected subgraphs

## Publications
Tempelmeier, N., Dietze, S. & Demidova, E. (2020). Crosstown Traffic - Supervised Prediction of Impact of planned Special Events on Urban Traffic. 
GeoInformatica. An International Journal on Advances of Computer Science for Geographic Information Systems, 24, 339-370. 
doi: https://doi.org/10.1007/s10707-019-00366-x

```
@article{tempelmeier2019crosstown,
  author = {Tempelmeier, Nicolas and Dietze, Stefan and Demidova, Elena},
  doi = {https://doi.org/10.1007/s10707-019-00366-x},
  issn = {1384-6175},
  journal = {GeoInformatica. An International Journal on Advances of Computer Science for Geographic Information Systems},
  keywords = {data4urbanmobility myown tempelmeier},
  number = 2,
  pages = {339-370},
  title = {Crosstown Traffic - Supervised Prediction of Impact of
Planned Special Events on Urban Traffic},
  url = {https://link.springer.com/article/10.1007%2Fs10707-019-00366-x},
  volume = 24,
  year = 2020
}
```
