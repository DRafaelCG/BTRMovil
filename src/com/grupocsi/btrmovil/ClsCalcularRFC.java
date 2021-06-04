package com.grupocsi.btrmovil;

public class ClsCalcularRFC {
	public ClsCalcularRFC(){
	}
	
	public String calcularRFC(String pNombres, String pApPat, String pApMat, String pFechaNac){
		String year = "";
		String mes = "";
		String dia = "";
		String rfcCalculado = "";
		String rfcPat = "";
		String rfcMat = "";
		String rfcVocalApPat = "";
		String rfcLetras = "";
		year = pFechaNac.toString().substring(8, 10);
		mes = pFechaNac.toString().substring(3, 5);
		dia = pFechaNac.toString().substring(0, 2);
		if (pApMat.toString().matches("")) {
			pApMat = "X";
		}
		pApPat = pApPat.toString().toUpperCase();
		pApPat = pApPat.toString().replace("DEL ", "");
		pApPat = pApPat.toString().replace("DE LAS ", "");
		pApPat = pApPat.toString().replace("DE LOS ", "");
		pApPat = pApPat.toString().replace("DE LA ", "");
		pApPat = pApPat.toString().replace("DE LO ", "");
		pApPat = pApPat.toString().replace("DE ", "");
		pApPat = pApPat.toString().replace("DA ", "");
		pApPat = pApPat.toString().replace("D ", "");
		pApPat = pApPat.toString().replace("DES ", "");
		pApPat = pApPat.toString().replace("DU ", "");
		pApPat = pApPat.toString().replace("VON ", "");
		pApPat = pApPat.toString().replace("VANDEN ", "");
		pApPat = pApPat.toString().replace("VANDER ", "");
		pApPat = pApPat.toString().replace("VAN ", "");
		pApPat = pApPat.toString().replaceAll("Y ", "");
		pApPat = pApPat.toString().replaceAll("Á", "A");
		pApPat = pApPat.toString().replaceAll("É", "E");
		pApPat = pApPat.toString().replaceAll("Í", "I");
		pApPat = pApPat.toString().replaceAll("Ó", "O");
		pApPat = pApPat.toString().replaceAll("Ú", "U");
		pApPat = pApPat.toString().replaceAll("Ü", "U");
		if ((pApPat.toString().contains("A")) ||  (pApPat.toString().contains("E")) || (pApPat.toString().contains("I")) || 
				(pApPat.toString().contains("O")) || (pApPat.toString().contains("U"))) {
			for (int i = 1; i < pApPat.length(); i++) {
				if (pApPat.toString().substring(i, i+1).contains("A")) {
					rfcVocalApPat = pApPat.toString().substring(i, i+1);
					break;
				}else if (pApPat.toString().substring(i, i+1).contains("E")) {
					rfcVocalApPat = pApPat.toString().substring(i, i+1);
					break;
				}else if (pApPat.toString().substring(i, i+1).contains("I")) {
					rfcVocalApPat = pApPat.toString().substring(i, i+1);
					break;
				}else if (pApPat.toString().substring(i, i+1).contains("O")) {
					rfcVocalApPat = pApPat.toString().substring(i, i+1);
					break;
				}else if (pApPat.toString().substring(i, i+1).contains("U")) {
					rfcVocalApPat = pApPat.toString().substring(i, i+1);
					break;
				}
			}	
		}else {
			rfcVocalApPat = "X";
		}
		rfcPat = pApPat.toString().substring(0, 1) + rfcVocalApPat.toString();
		pApMat = pApMat.toString().toUpperCase();
		pApMat = pApMat.toString().replace("DEL ", "");
		pApMat = pApMat.toString().replace("DE LAS ", "");
		pApMat = pApMat.toString().replace("DE LOS ", "");
		pApMat = pApMat.toString().replace("DE LA ", "");
		pApMat = pApMat.toString().replace("DE LO ", "");
		pApMat = pApMat.toString().replace("DE ", "");
		pApMat = pApMat.toString().replace("DA ", "");
		pApMat = pApMat.toString().replace("D ", "");
		pApMat = pApMat.toString().replace("DES ", "");
		pApMat = pApMat.toString().replace("DU ", "");
		pApMat = pApMat.toString().replace("VON ", "");
		pApMat = pApMat.toString().replace("VANDEN ", "");
		pApMat = pApMat.toString().replace("VANDER ", "");
		pApMat = pApMat.toString().replace("VAN ", "");
		pApMat = pApMat.toString().replace("Y ", "");
		pApMat = pApMat.toString().replaceAll("Á", "A");
		pApMat = pApMat.toString().replaceAll("É", "E");
		pApMat = pApMat.toString().replaceAll("Í", "I");
		pApMat = pApMat.toString().replaceAll("Ó", "O");
		pApMat = pApMat.toString().replaceAll("Ú", "U");
		pApMat = pApMat.toString().replaceAll("Ü", "U");
		rfcMat = pApMat.toString().substring(0, 1);
		pNombres = pNombres.toString().toUpperCase();
		
		pNombres = pNombres.toString().replace("DEL ", "");
		pNombres = pNombres.toString().replace("DE LAS ", "");
		pNombres = pNombres.toString().replace("DE LOS ", "");
		pNombres = pNombres.toString().replace("DE LA ", "");
		pNombres = pNombres.toString().replace("DE LO ", "");
		pNombres = pNombres.toString().replace("DE ", "");
		pNombres = pNombres.toString().replace("DA ", "");
		pNombres = pNombres.toString().replace("D ", "");
		pNombres = pNombres.toString().replace("DES ", "");
		pNombres = pNombres.toString().replace("DU ", "");
		pNombres = pNombres.toString().replace("VON ", "");
		pNombres = pNombres.toString().replace("VANDEN ", "");
		pNombres = pNombres.toString().replace("VANDER ", "");
		pNombres = pNombres.toString().replace("VAN ", "");
		pNombres = pNombres.toString().replace("Y ", "");
		pNombres = pNombres.toString().replaceAll("Á", "A");
		pNombres = pNombres.toString().replaceAll("É", "E");
		pNombres = pNombres.toString().replaceAll("Í", "I");
		pNombres = pNombres.toString().replaceAll("Ó", "O");
		pNombres = pNombres.toString().replaceAll("Ú", "U");
		pNombres = pNombres.toString().replaceAll("Ü", "U");
		if (pNombres.toString().contains(" ")) {
			pNombres = pNombres.replace("MARIA JOSE", "JOSES");
			pNombres = pNombres.replace("JOSE MARIA", "MARIAS");
			pNombres = pNombres.replace("MARIA ", "");
			pNombres = pNombres.replace("JOSE ", "");
			pNombres = pNombres.replace("MA ", "");
			pNombres = pNombres.replace("MA. ", "");
		}
		rfcLetras =  rfcPat + rfcMat + pNombres.toString().substring(0, 1);
		rfcLetras = igualGroceria(rfcLetras);
		rfcCalculado = rfcLetras + year + mes + dia;
		return rfcCalculado;
	}
	
	public String igualGroceria(String rfcLetras){
		String rfcNoGrocerias = "";
		if ((rfcLetras.toString().equals("BUEI"))) {
			rfcNoGrocerias = "BUEX";
		}else if ((rfcLetras.toString().equals("BUEY"))) {
			rfcNoGrocerias = "BUEX";
		}else if ((rfcLetras.toString().equals("CACA"))) {
			rfcNoGrocerias = "CACX";
		}else if ((rfcLetras.toString().equals("CACO"))) {
			rfcNoGrocerias = "CACX";
		}else if ((rfcLetras.toString().equals("CAGA"))) {
			rfcNoGrocerias = "CAGX";
		}else if ((rfcLetras.toString().equals("CAGO"))) {
			rfcNoGrocerias = "CAGX";
		}else if ((rfcLetras.toString().equals("CAKA"))) {
			rfcNoGrocerias = "CAKX";
		}else if ((rfcLetras.toString().equals("CAKO"))) {
			rfcNoGrocerias = "CAKX";
		}else if ((rfcLetras.toString().equals("COGE"))) {
			rfcNoGrocerias = "COGX";
		}else if ((rfcLetras.toString().equals("COJA"))) {
			rfcNoGrocerias = "COJX";
		}else if ((rfcLetras.toString().equals("COJE"))) {
			rfcNoGrocerias = "COJX";
		}else if ((rfcLetras.toString().equals("COJI"))) {
			rfcNoGrocerias = "COJX";
		}else if ((rfcLetras.toString().equals("COJO"))) {
			rfcNoGrocerias = "COJX";
		}else if ((rfcLetras.toString().equals("CULO"))) {
			rfcNoGrocerias = "CULX";
		}else if ((rfcLetras.toString().equals("FETO"))) {
			rfcNoGrocerias = "FETX";
		}else if ((rfcLetras.toString().equals("GUEY"))) {
			rfcNoGrocerias = "GUEX";
		}else if ((rfcLetras.toString().equals("JOTO"))) {
			rfcNoGrocerias = "JOTX";
		}else if ((rfcLetras.toString().equals("KACA"))) {
			rfcNoGrocerias = "KACX";
		}else if ((rfcLetras.toString().equals("KACO"))) {
			rfcNoGrocerias = "KACX";
		}else if ((rfcLetras.toString().equals("KAGA"))) {
			rfcNoGrocerias = "KAGX";
		}else if ((rfcLetras.toString().equals("KAGO"))) {
			rfcNoGrocerias = "KAGX";
		}else if ((rfcLetras.toString().equals("KAKA"))) {
			rfcNoGrocerias = "KAKX";
		}else if ((rfcLetras.toString().equals("KOGE"))) {
			rfcNoGrocerias = "KOGX";
		}else if ((rfcLetras.toString().equals("KOJO"))) {
			rfcNoGrocerias = "KOJX";
		}else if ((rfcLetras.toString().equals("KULO"))) {
			rfcNoGrocerias = "KULX";
		}else if ((rfcLetras.toString().equals("MAME"))) {
			rfcNoGrocerias = "MAMX";
		}else if ((rfcLetras.toString().equals("MAMO"))) {
			rfcNoGrocerias = "MAMX";
		}else if ((rfcLetras.toString().equals("MEAR"))) {
			rfcNoGrocerias = "MXAR";
		}else if ((rfcLetras.toString().equals("MEAS"))) {
			rfcNoGrocerias = "MXAS";
		}else if ((rfcLetras.toString().equals("MEON"))) {
			rfcNoGrocerias = "MXON";
		}else if ((rfcLetras.toString().equals("MION"))) {
			rfcNoGrocerias = "MXON";
		}else if ((rfcLetras.toString().equals("MOCO"))) {
			rfcNoGrocerias = "MOCX";
		}else if ((rfcLetras.toString().equals("MULA"))) {
			rfcNoGrocerias = "MULX";
		}else if ((rfcLetras.toString().equals("PEDA"))) {
			rfcNoGrocerias = "PEDX";
		}else if ((rfcLetras.toString().equals("PEDO"))) {
			rfcNoGrocerias = "PEDX";
		}else if ((rfcLetras.toString().equals("PENE"))) {
			rfcNoGrocerias = "PENX";
		}else if ((rfcLetras.toString().equals("PUTA"))) {
			rfcNoGrocerias = "PUTX";
		}else if ((rfcLetras.toString().equals("PUTO"))) {
			rfcNoGrocerias = "PUTX";
		}else if ((rfcLetras.toString().equals("QULO"))) {
			rfcNoGrocerias = "QULX";
		}else if ((rfcLetras.toString().equals("RATA"))) {
			rfcNoGrocerias = "RATX";
		}else if ((rfcLetras.toString().equals("RUIN"))) {
			rfcNoGrocerias = "RXIN";
		}else if ((rfcLetras.toString().equals("COLA"))) {
			rfcNoGrocerias = "COLX";
		}else if ((rfcLetras.toString().equals("LOCA"))) {
			rfcNoGrocerias = "LOCX";
		}else {
			rfcNoGrocerias = rfcLetras;
		}
		return rfcNoGrocerias;
	}
}
