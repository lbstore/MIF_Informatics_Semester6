Reali ir virtuali mašina
Lukas Klusis,  2014 03 24

šios mašinos papildomi reikalavimai:
	* dirba su steku
	* yra paketinė (t.y. duomenis paduoda kartu su programos kodu ir interaktyviai su vartotoju nedirba)
	* turi FORK metodą (procesų dubliavimas)
	
KĄ REIKIA PAKEISTI:
	
1. Nėra saugoma puslapių lentelė realios mašinos atmintyje:
	Ką reikia padaryti:
	* kuriant virtualią mašiną reikia allocatinti realios mašinos atmintį (kaip supervizorinę atmintį, kurią redaguoti gali tik reali mašina, arba tiksliau ta virtuali mašina per realią mašiną). Jeigu virtuali mašina yra 16 puslapių, tai lentelę turi sudaryti taip pat 16 įrašų
	* vieną įrašą galėtų sudaryti: 1bitas - IsAllocated 2bitai- realios mašinos puslapio numeris ir tarkime virtualiai mašinai paskirtas registras kuris saugo addresą į šią lentelę
	
	aišku.... galėtų būtų dar geriau, kaip nors realizuota pagal Tannenbaum knygą ;D bet who cares...
	
2. Duomenis reikėtų rašyti anksčiau negu programos kodą ir tada visą likusių vietą paskirti stack'ui


KĄ REIKTŲ PAKEISTI:

3. Jeigu yra noro papildomai pridėti informaciją apie realią mašiną (registrai and stuff...)
4. Word klasę reiktų perrašyti iš naujo...
5. galima pagalvoti naudingesnes komandas:
	* įterpti komentarų programos kode galimybes
	* leisti programos kodo komandas ilgesnes negu 4 simboliai

	
	
