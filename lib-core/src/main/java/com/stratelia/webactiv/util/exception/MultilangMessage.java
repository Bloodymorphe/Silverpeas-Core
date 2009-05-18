package com.stratelia.webactiv.util.exception;

public class MultilangMessage  {

  private String message = null;
  private String[] params;

  /**
   * fabrication d'un message multilangue avec un param�tre.
   * Le message est un label multilangue qui correspond, une fois traduit � une chaine contenant un param�tre.
   * Exemple :
   * message = "util.MSG_EJB_INTROUVABLE", param1 = "ejb/NodeHome".
   * Traduction dans fichier properties francais "util.MSG_EJB_INTROUVABLE = L'ejb nomm� %1 est introuvable".
   * %1 est le premier param�tre. On imprimera "L'ejb nomm� ejb/NodeHome est introuvable".
   */
	public MultilangMessage(String message, String param1) {
    this.message = message;
    params = new String[1];
    params[0] = param1;
	}

  /**
   * meme chose avec deux param�tres. (%1 et %2)
   */
	public MultilangMessage(String message, String param1, String param2) {
    this.message = message;
    params = new String[2];
    params[0] = param1;
    params[1] = param2;
	}

  // remarque : on pourrait continuer avec trois param�tres. Dans ce cas, voir aussi la methode fromString()

  public String getMessage() {
    return message;
  }

  public String[] getParameters() {
    return params;
  }

  /**
   * codage du message multilangue et de ses param�tre sous forme d'une String
   * @return la chaine encod�e, qq chose comme [message,param1,param2]
   */
  public String toString() {
    String result = "[" + message ;
    for (int i = 0 ; i < params.length; i++) {
      result += "," + params[i];
    }
    result += "]";
    return result;
  }
}