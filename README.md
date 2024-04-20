# FrontEnd
# Lentopaikat.fi sovelluksen kehitys

Kehittäjät:
[Jaakko](https://github.com/JaakkoSai), [Juho](https://github.com/Juho14), [Matias](https://github.com/VEHICLE-PICK-KB), [Zorana](https://github.com/zokaas)


# Sovelluksen tarkoitus

Sovelluksen tarkoitus on helpottaa loppukäyttäjää tarkastelemaan lentoasemien tankkausmahdollisuuksia ja hintoja. Projektin tavoite on kehittää olemassaolevaa [lentopaikat.fi](http://lentopaikat.fi) sovellusta, joka palvelee ilmailuyhteisöä tarjoamalla kattavat tiedot Suomen lentopaikoista.

# Ominaisuudet

- **Lentopaikat:** Tallennettuna lentopaikat, joilla on jonkinlainen virallinen polttoainepiste.
- **Sijainti:** Lentopaikan sijainti yksinkertaiseen karttaan merkittynä, samalla tavalla kuin [lentopaikat.fi](http://lentopaikat.fi) palvelussa.
- **Polttoaineet:** Tieto siitä, mitä polttoaineita lentopaikasta saa. Käyttäjät voivat ilmoittaa, jos polttoaine on loppu, jolloin tilanne muuttuu ns. "punaiseksi".
- **Yhteystiedot:** Lista nimiä ja puhelinnumeroita, joista polttoainetta tai apuja sen saamiseen voi tiedustella.
- **Maksumahdollisuudet:** Polttoaineen maksumahdollisuudet kuten käteinen, MobilePay, tilisiirto ja MobilePay.
- **Kirjautuminen:** Järjestelmään kirjautuminen esimerkiksi ilmailuliiton jäsennumerolla.

# Kehityksessä käytettävät teknologiat

- **Java OpenJDK 11:** [Lataa](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- **React 18:** [Versiot](https://legacy.reactjs.org/versions/)
- **Node.js (versiot 18-20):** [Lataa](https://nodejs.org/en)
- **Visual Studio Code:** [Lataa](https://code.visualstudio.com/download)
- **VSCode Lisäosat:** ESLint, Liveshare, Live Server, Prettier.


# Lentopaikat sovelluksen näkymät

![Lentopaikat](https://github.com/OP2-Black/FrontEnd/assets/97917358/87730137-0cfb-4680-b458-af701934d65f)
*Kokonaisnäkymä lentopaikat.fi sovelluksesta*

![EFMI](https://github.com/OP2-Black/FrontEnd/assets/97917358/baf04f29-f85e-4cec-84f8-c77f43503bad)
*EFMI lentopaikan näkymä*

![JSON_data_page](https://github.com/OP2-Black/FrontEnd/assets/97917358/77e85d49-b0e5-4261-9770-529c55ec721a)
*Esimerkki sovelluksen käyttämästä JSON-datasta*

![JSON_data_page_EFMI](https://github.com/OP2-Black/FrontEnd/assets/97917358/6a327999-47d3-46f5-b105-4ac8c4b2edf1)
*EFMI lentopaikan tiedot JSON-muodossa*


Nämä kuvat antavat käsityksen siitä, miltä sovellus näyttää käyttäjän näkökulmasta ja millaista dataa se käsittelee.

# Crontabin käyttö tiedon automaattiseen päivitykseen

Tässä projektissa hyödynnämme crontab-ohjelmaa automatisoidaksemme prosessin, jossa haemme viimeisimmät tiedot palvelimeltamme 15 minuutin välein. Crontab on Unix-pohjaisten käyttöjärjestelmien aikataulutustyökalu, joka mahdollistaa komentojen suorittamisen säännöllisesti määriteltynä aikana.

## Asetusten konfigurointi

1. **SSH-yhteys:** Varmistetaan, että sinulla on SSH-yhteys palvelimeen.
2. **Java-sovelluksen suoritus:** Käytämme Java-sovellusta nimeltä ProxyPalvelu tiedon noutamiseen ja tallentamiseen JSON-muodossa.

## Crontab-määritys

Crontabin komenton suorituksen ajastamiseen käytetään tähtiä (`*`). Käytössä on viisi tähteä, jossa jokainen merkitsee ajankohtaa:

1. Minuutti (0 - 59)
2. Tunti (0 - 23)
3. Kuukauden päivä (1 - 31)
4. Kuukausi (1 - 12)
5. Viikonpäivä (0 - 6), jossa 0 tarkoittaa sunnuntaita, 1 maanantaita jne.


### Komennot

Muokkaa olemassa olevaa konfiguraatiota

`crontab -e`

Listaa crontab-tehtävät

`crontab -l`

Tässä projektissa haluamme, että tehtävä suoritetaan 15 min välein. Se suoritetaan jokaisen tunnin 1, 15, 30, 45 minuuttien kohdalla. 
Komento:
1. Siirtyy hakemistoon, jossa Java-sovellus sijaitsee (`cd /home/users/jtjuslin/www/kananen/javat/lib/`).
2. Suorittaa ProxyPalvelu Java-sovelluksen, joka hakee viimeisimmät tiedot.
3. Tallentaa haetut tiedot `data.json` tiedostoon oikeassa sijainnissa.

```bash
1,15,30,45 * * * * cd /home/users/jtjuslin/www/kananen/javat/lib/; java ProxyPalvelu > /home/users/jtjuslin/www/kananen/data.json
```
