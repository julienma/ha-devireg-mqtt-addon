# Devismart MQTT (patched)

Basé sur [bob-tm/ha-apps](https://github.com/bob-tm/ha-apps) 0.5 et
[igor-podpalchenko/ha-devi-mqtt](https://github.com/igor-podpalchenko/ha-devi-mqtt), avec la liste
complète des serveurs du cloud Danfoss et une reconnexion fiabilisée (voir le CHANGELOG).

Les noms ci-dessous (`72ae8254_…`) supposent que le dépôt a été ajouté avec l'URL exacte
`https://github.com/julienma/ha-devireg-mqtt-addon`. En cas de doute, le vrai nom du conteneur
est donné par `docker ps --format '{{.Names}}' | grep devismart`.

## 1. Configuration

Onglet **Configuration** : hôte `core-mosquitto` (broker Mosquitto de HA), port `1883`,
utilisateur et mot de passe MQTT.

Au premier démarrage, le journal affiche `devi_config.json not found` / `Running in waiting mode` :
l'add-on attend d'être appairé.

Si l'add-on Devismart MQTT d'origine est installé, l'arrêter : les deux utilisent le port 8000.

## 2. Prérequis à l'appairage : tous les thermostats joignables

L'app ECtemp Smart **refuse de générer un code OTP tant qu'un seul thermostat de la maison est
injoignable**. Ouvrir l'app et attendre que tous les thermostats y soient affichés ; enchaîner
l'appairage tant que c'est le cas.

Avant d'appairer, supprimer dans l'app les accès partagés obsolètes : chaque thermostat n'a que
10 emplacements d'appairage.

## 3. Appairer

1. Installer l'add-on **Advanced SSH & Web Terminal**, désactiver son *Protection mode*,
   l'ouvrir.
2. Lancer l'appairage :
   ```bash
   docker exec -it app_72ae8254_devismart-mqtt /config.sh
   ```
3. Dans l'app : Réglages → Partager la maison, et saisir le code **sans les tirets**. Le premier
   code échoue souvent : en régénérer un et recommencer. L'étape peut être longue.

## 4. Sauvegarder l'identité — immédiatement

```bash
docker exec app_72ae8254_devismart-mqtt cat /config/devi_config.json
```

(Le dossier de configuration de l'add-on, `/config` dans le conteneur, est
`/mnt/data/supervisor/app_configs/72ae8254_devismart-mqtt/` sur l'hôte ; le terminal SSH ne le
monte pas, d'où le `docker exec`.)

Copier la sortie dans un gestionnaire de mots de passe. Elle contient la clé privée du pont : la
perdre oblige à ré-appairer, donc à réunir à nouveau tous les thermostats.

## 5. Vérifier

Redémarrer l'add-on. Dans le journal :

- `Config Exists!` puis `Successfully connected to Danfoss grid` ;
- `Connection established` pour chaque thermostat ;
- en cas d'échec : `Reconnecting to peer … in N s`, N augmentant de 10 à 300 s.

Les thermostats apparaissent dans l'intégration MQTT. Un thermostat peut mettre quelques minutes
à se connecter : certains ne restent joignables sur le cloud que par intermittence, l'add-on
réessaie jusqu'à tomber dans une fenêtre, puis la connexion tient.

## Personnalisation des entités

Les modèles d'auto-discovery peuvent être surchargés : copier le dossier
`auto-discovery-templates` dans le dossier de configuration de l'add-on (`/config` dans le
conteneur) et modifier les JSON :
```bash
docker exec app_72ae8254_devismart-mqtt cp -r /app/auto-discovery-templates /config/
```
Ils sont prioritaires sur ceux intégrés à l'add-on.
