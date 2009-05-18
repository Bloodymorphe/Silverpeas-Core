package com.stratelia.webactiv.util.exception;

import java.util.Collection;
import java.util.Iterator;

import com.stratelia.silverpeas.silvertrace.SilverTrace;

/**
 * SilverpeasRuntimeException est la racine de la hi�rarchie des exception
 * runtime silverpeas. Les exceptions Runtime ne sont pas obligatoirement
 * trait�es par le d�veloppeur. C'est pourquoi leur utilisation doit se faire
 * apr�s reflexion. La principale application des SilverpeasRuntimeException est
 * l'utilisation au sein des EJB. En effet une RuntimeException qui traverse le
 * conteneur annule la transaction en cours, et sera encapsul�e dans une
 * RemoteException. Le message que l'on donne � l'exception est tr�s important,
 * il doit etre affich� � l'utilisateur. C'est pourquoi le label est
 * multilangue. Chaque classe heritant de SilverpeasRuntimeException doit
 * surdefinir la fonction getModule qui retourne le nom du module (le meme nom
 * que celui defini dans Silvertrace)
 */
abstract public class SilverpeasRuntimeException extends RuntimeException
    implements WithNested, FromModule {
  public static final int FATAL = SilverTrace.TRACE_LEVEL_FATAL;
  public static final int ERROR = SilverTrace.TRACE_LEVEL_ERROR;
  public static final int WARNING = SilverTrace.TRACE_LEVEL_WARN;

  private int errorLevel = ERROR;
  private Exception nested = null;
  private String extraParams = "";
  private String callingClass = "NoClass";

  // Constructors
  // ------------

  /**
   * Fabriquation d'une exception runtime silverpeas avec un message
   * multilangue. Il n'y a pas d'exception ayant provoqu� celle-ci (getNested()
   * renvera null).
   * 
   * @param message
   *          Le label multilangue. Ex : "impossibleDeFabriquerUneConnexionBDD".
   *          Le label est traduit dans des fichiers de properties.
   * @deprecated
   */
  public SilverpeasRuntimeException(String message) {
    this("noClass", ERROR, message, null, null);
  }

  /**
   * Fabriquation d'une exception runtime silverpeas avec une exception �
   * imbriquer. L'exception ne contient pas de message d'erreur. On utilisera ce
   * constructeur lorsque l'on veut encapsuler une exception (afin de casser une
   * transaction), mais que l'on n'a pas d'info � ajouter.
   * 
   * @param nested
   *          L'exception qui a provoqu� le probl�me. nested peut etre une
   *          SilverpeasException ou une exception technique (SQLException,
   *          RemoteException...)
   * @deprecated
   */
  public SilverpeasRuntimeException(Exception nested) {
    this("noClass", ERROR, "root.MSG_EX_NO_MESSAGE", null, nested);
  }

  /**
   * Fabriquation d'une exception runtime silverpeas avec un message multilangue
   * et une exception � imbriquer.
   * 
   * @param message
   *          Le label multilangue. Ex : "impossibleDeFabriquerUneConnexionBDD".
   *          Le label est traduit dans des fichiers de properties.
   * @param nested
   *          L'exception qui a provoqu� le probl�me. nested peut etre une
   *          SilverpeasException ou une exception technique (SQLException,
   *          RemoteException...)
   * @deprecated
   */
  public SilverpeasRuntimeException(String message, Exception nested) {
    this("noClass", ERROR, message, null, nested);
  }

  public SilverpeasRuntimeException(String callingClass, int errorLevel,
      String message) {
    this(callingClass, errorLevel, message, null, null);
  }

  public SilverpeasRuntimeException(String callingClass, int errorLevel,
      String message, String extraParams) {
    this(callingClass, errorLevel, message, extraParams, null);
  }

  public SilverpeasRuntimeException(String callingClass, int errorLevel,
      String message, Exception nested) {
    this(callingClass, errorLevel, message, null, nested);
  }

  public SilverpeasRuntimeException(String callingClass, int errorLevel,
      String message, String extraParams, Exception nested) {
    super(message);
    this.callingClass = callingClass;
    this.errorLevel = errorLevel;
    this.extraParams = extraParams;
    this.nested = nested;

    // Trace the exception as INFO or FATAL
    if (errorLevel == FATAL) {
      SilverTrace.fatal(getModule(), callingClass, message, extraParams, this);
    } else {
      SilverTrace.info(getModule(), callingClass, message, extraParams, this);
    }
  }

  // WithNested methods
  // ------------------

  /**
   * retourne l'exception qui a provoqu� la creation de celle-ci. Permet
   * l'encapsulation des exception technique.
   * 
   * @return L'exception pr�c�dente qui a provoqu� celle-ci, null s'il n'y en a
   *         pas.
   */
  public Exception getNested() {
    return nested;
  }

  // Throwable methods overloaded
  // ----------------------------

  public void printStackTrace() {
    this.printStackTrace(System.out);
  }

  public void printStackTrace(java.io.PrintStream s) {
    if (getNested() == null) {
      s.println(this.getMessageLang());
      super.printStackTrace(s);
    } else {
      Throwable e = null;

      s.println(this.getMessageLang());
      Collection<Throwable> exceptions = SilverpeasException
          .getChainedExceptions(getNested());
      for (Iterator<Throwable> i = exceptions.iterator(); i.hasNext();) {
        e = (Throwable) i.next();
        if (!i.hasNext()) {
          s.println("nested (Deepest) : ");
        } else {
          if (e instanceof FromModule) {
            s.println("nested : " + ((FromModule) e).getMessageLang());
          } else {
            s.println("nested : " + e.getMessage());
          }
        }
      }
      e.printStackTrace(s);
    }
  }

  public void printStackTrace(java.io.PrintWriter w) {
    if (getNested() == null) {
      w.println(this.getMessageLang());
      super.printStackTrace(w);
    } else {
      Throwable e = null;
      w.println(this.getMessageLang());
      Collection<Throwable> exceptions = SilverpeasException
          .getChainedExceptions(getNested());
      for (Iterator<Throwable> i = exceptions.iterator(); i.hasNext();) {
        e = (Throwable) i.next();
        if (!i.hasNext()) {
          w.println("nested (Deepest) : ");
        } else {
          if (e instanceof FromModule) {
            w.println("nested : " + ((FromModule) e).getMessageLang());
          } else {
            w.println("nested : " + e.getMessage());
          }
        }
      }
      e.printStackTrace(w);
    }
  }

  // FromModule methods
  // ------------------

  /**
   * This function must be defined by the Classes that herit from this one
   * 
   * @return The SilverTrace's module name
   **/
  abstract public String getModule();

  public String getMessageLang() {
    return SilverTrace.getTraceMessage(getMessage());
  }

  public String getMessageLang(String language) {
    return SilverTrace.getTraceMessage(getMessage(), language);
  }

  public void traceException() {
    switch (errorLevel) {
      case FATAL:
        SilverTrace.fatal(getModule(), callingClass, getMessage(), extraParams,
            this);
        break;
      case ERROR:
        SilverTrace.error(getModule(), callingClass, getMessage(), extraParams,
            this);
        break;
      case WARNING:
        SilverTrace.warn(getModule(), callingClass, getMessage(), extraParams,
            this);
        break;
    }
  }

  /**
   * retourne le niveau de critissicit� de l'exception
   * 
   * @return le niveau d'erreur, FATAL, ERROR, ou WARNING
   */
  public int getErrorLevel() {
    return errorLevel;
  }
}
