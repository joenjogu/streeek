# Modules

## Folders
Streeek has 6 module folders

| Folder           | Description                                         |
|------------------|-----------------------------------------------------|
| modules-app      | all application modules                             |
| modules-ui       | contains shared ui module                           |
| modules-features | contains all feature modules                        |
| modules-data     | contains all data accessors and aggregators modules |
| modules-sources  | contains all data sources modules                   |

## Modules
| Module       | Folder      | Description                                        | Dependency                       |
|:-------------|-------------|----------------------------------------------------|:---------------------------------|
| streeek      | modules-app | mobile app module                                  | `presentation`, `common`, `data` |
| presentation | modules-ui  | module common app starting things e.g MainActivity | `common`, `domain`               |
