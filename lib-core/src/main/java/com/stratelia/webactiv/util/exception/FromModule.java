package com.stratelia.webactiv.util.exception;

/**
 * Cette interface doit etre impl�ment�e par toutes les exception souhaitant
 * �tre monitor�s. La m�thode getModule() doit �tre implement�e pour permettre
 * de connaitre le nom du module ayant g�n�r� cette exception. Par exemple, pour
 * le module d'administration, on va d�finir une AdminException qui "extends"
 * SilverpeasException, et "implements" FromModuleException. La m�thode
 * getModule devra renvoyer une chaine du style "Admin".
 * 
 */
public interface FromModule {
  /**
   * This function must be defined by the Classes that herit from this one
   * 
   * @return The SilverTrace's module name
   **/
  public String getModule();

  public String getMessageLang();

  public String getMessageLang(String language);

  public void traceException();

  public int getErrorLevel();
}
