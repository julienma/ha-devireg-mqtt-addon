# Devismart MQTT (patched) — dépôt d'add-on Home Assistant

Add-on Home Assistant pour les thermostats Danfoss **ECtemp Smart / DEVIreg Smart**, exposés dans
HA via MQTT. Version corrigée de [bob-tm/ha-apps](https://github.com/bob-tm/ha-apps) 0.5,
elle-même basée sur [igor-podpalchenko/ha-devi-mqtt](https://github.com/igor-podpalchenko/ha-devi-mqtt)
et [OpenSDG](https://github.com/Sonic-Amiga/opensdg-java).

## Ce qui change par rapport à l'original

- **Liste complète des 8 serveurs du cloud Danfoss** (SecureDeviceGrid). opensdg n'en connaissait
  que 4 : les thermostats enregistrés sur les autres étaient injoignables
  (`Connection refused by grid: 1`).
- Thread dédié pour le keepalive du cloud.
- Une connexion au cloud en échec est jetée au lieu d'être réutilisée dans un état cassé.
- Reconnexion avec backoff exponentiel et gigue (10 s → 5 min) au lieu d'un retry fixe de 10 s.
- Toute erreur pendant la connexion déclenche une reconnexion, sans doublons.
- Le code Java corrigé est embarqué dans [`devismart-mqtt/bridge/`](devismart-mqtt/bridge/) : le
  build ne clone plus rien sur GitHub.

Détail dans le [CHANGELOG](devismart-mqtt/CHANGELOG.md).

## Installation

[![Ajouter le dépôt à Home Assistant](https://my.home-assistant.io/badges/supervisor_add_addon_repository.svg)](https://my.home-assistant.io/redirect/supervisor_add_addon_repository/?repository_url=https%3A%2F%2Fgithub.com%2Fjulienma%2Fha-devireg-mqtt-addon)

Ou à la main : **Paramètres → Modules complémentaires → Boutique → ⋮ → Dépôts**, ajouter
exactement :

```
https://github.com/julienma/ha-devireg-mqtt-addon
```

Sans `/` final ni `.git` : HA dérive le nom interne de l'add-on de cette URL. Avec celle-ci,
le conteneur s'appelle `app_72ae8254_devismart-mqtt` et sa configuration est dans
`/mnt/data/supervisor/app_configs/72ae8254_devismart-mqtt/` sur l'hôte (les noms utilisés dans la
documentation ; les versions de HA antérieures aux « apps » utilisaient le préfixe `addon_`).

Installer ensuite **Devismart MQTT (patched)**. L'add-on est compilé sur la machine HA :
compter 10 à 20 minutes. Architectures : `aarch64`, `amd64`.

⚠️ Si l'add-on Devismart MQTT d'origine est installé, l'arrêter (et désactiver son démarrage
automatique) : les deux utilisent le port 8000.

La configuration, l'appairage des thermostats et la sauvegarde de l'identité sont décrits
dans l'onglet **Documentation** de l'add-on ([DOCS.md](devismart-mqtt/DOCS.md)).

## Mettre à jour la liste des serveurs

Les thermostats et l'app n'utilisent aucun DNS : les serveurs du cloud sont des IP en dur. Si
Danfoss en ajoute, le pont ne les connaîtra pas. À vérifier en cas de nouveaux
`Connection refused by grid: 1`.

Depuis un routeur qui voit le trafic des thermostats (par exemple une gateway UniFi en SSH),
remplacer `<interface>` par l'interface du réseau des thermostats et les `<ip_thermostat_N>`
par leurs adresses :

```bash
tcpdump -i <interface> -n 'tcp[tcpflags] & tcp-syn != 0 and tcp[tcpflags] & tcp-ack == 0 and dst port 443 and (src host <ip_thermostat_1> or src host <ip_thermostat_2>)'
```

Les thermostats ne parlent qu'au cloud : toute destination en 443 est un serveur. Les
connexions durent longtemps ; couper le courant d'un thermostat pendant la capture pour le voir
choisir un serveur.

Liste au 2026-09-08, dans
[`DanfossGrid.java`](devismart-mqtt/bridge/src/main/java/io/github/sonic_amiga/opensdg/java/DanfossGrid.java) :

```
77.66.11.90   77.66.11.92   77.66.7.41    77.66.7.42
5.179.92.171  5.179.92.172  5.179.92.180  5.179.92.182
```

Pour ajouter une IP : la mettre dans `DanfossGrid.java`, incrémenter `version` dans
[`devismart-mqtt/config.yaml`](devismart-mqtt/config.yaml), pousser. HA proposera la mise à jour.
