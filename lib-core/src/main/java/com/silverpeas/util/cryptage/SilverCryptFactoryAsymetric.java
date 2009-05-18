package com.silverpeas.util.cryptage;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.KeyTransRecipientInformation;

import com.stratelia.webactiv.util.exception.SilverpeasException;

public class SilverCryptFactoryAsymetric
{
	// Singleton pour g�rer une seule Map de trousseaux de cl�s
	private static SilverCryptFactoryAsymetric factory = null;
	
	private SilverCryptFactoryAsymetric()
	{
	}
	
	private Map keyMap = new HashMap();
	
	public static SilverCryptFactoryAsymetric getInstance()
	{
		if (factory == null)
			factory = new SilverCryptFactoryAsymetric();
		
		return factory;
	}	
	
	public byte[] goCrypting(String stringUnCrypted, String fileName) throws CryptageException
	{
		try {
		        // Chargement de la chaine � crypter
				byte[] buffer = stringToByteArray(stringUnCrypted);
				
		        // Chiffrement du document
				CMSEnvelopedDataGenerator gen = new CMSEnvelopedDataGenerator();
		        // La variable cert correspond au certificat du destinataire
		        // La cl� publique de ce certificat servira � chiffrer la cl� sym�trique
		        gen.addKeyTransRecipient((java.security.cert.X509Certificate)this.getKeys(fileName).getCert());
		        
		        // Choix de l'algorithme � cl� sym�trique pour chiffrer le document.
		        // AES est un standard. Vous pouvez donc l'utiliser sans crainte.
		        // Il faut savoir qu'en france la taille maximum autoris�e est de 128
		        // bits pour les cl�s sym�triques (ou cl�s secr�tes)
		        String algorithm = CMSEnvelopedDataGenerator.AES128_CBC;
		        CMSEnvelopedData envData = gen.generate(
		                                        new CMSProcessableByteArray(buffer),
		                                        algorithm, "BC");
	
		        byte[] pkcs7envelopedData = envData.getEncoded();
		        
		        return  pkcs7envelopedData;
		        
		        //return String.valueOf(pkcs7envelopedData);
		        
		} catch (CryptageException e) {
	        throw e;
	    } catch (Exception e) {
				
	    	throw new CryptageException(
					"SilverCryptFactory.goCrypting",
					SilverpeasException.ERROR,
					"util.CRYPT_FAILED", e);
		}
	}
	
	public String goUnCrypting(byte[] stringCrypted, String fileName) throws CryptageException
	{
		try {
			// Chargement de la chaine � d�chiffrer
	        byte[] pkcs7envelopedData = stringCrypted;
			
	        // D�chiffrement de la chaine
	        CMSEnvelopedData ced = new CMSEnvelopedData(pkcs7envelopedData);
	        Collection recip = ced.getRecipientInfos().getRecipients();

	        KeyTransRecipientInformation rinfo = (KeyTransRecipientInformation)
	            recip.iterator().next();
	        // privatekey est la cl� priv�e permettant de d�chiffrer la cl� secr�te
	        // (sym�trique)
	        byte[] contents = rinfo.getContent(this.getKeys(fileName).getPrivatekey(), "BC");
	        
	        return byteArrayToString(contents);
	        
		} catch (CryptageException e) {
		        throw e;
		} catch (Exception e) {
			throw new CryptageException(
					"SilverCryptFactory.goUnCrypting",
					SilverpeasException.ERROR,
					"util.UNCRYPT_FAILED", e);
		}
	}
	
	public void addKeys(String filename, String password) throws CryptageException
	{// ajout d'une trousseau de cl� � partir d'un chemin d'un fichier p12 + password
		synchronized (keyMap)
		{
			if(this.keyMap.containsKey(filename))
			{
				throw new CryptageException(
						"SilverCryptFactory.addKeys",
						SilverpeasException.ERROR,
						"util.KEY_ALREADY_IN");
			}else
			{
				try
				{
					FileInputStream file = new FileInputStream(filename);
					SilverCryptKeysAsymetric silverkeys = new SilverCryptKeysAsymetric(file, password);
					
					this.keyMap.put(filename,silverkeys);					
				}catch(Exception e)
				{
					throw new CryptageException(
							"SilverCryptFactory.addKeys",
							SilverpeasException.ERROR,
							"util.KEYS_CREATION_FAILED");
				}
			}
		}
	}
	
	private SilverCryptKeysAsymetric getKeys(String filename) throws CryptageException
	{// r�cup�ration du trousseau de cl�!
		if(this.keyMap.containsKey(filename))
		{
			return (SilverCryptKeysAsymetric)this.keyMap.get(filename);
		}else
		{
			throw new CryptageException(
					"SilverCryptFactory.addKeys",
					SilverpeasException.ERROR,
					"util.KEY_NOT_FOUND");
		}
	}
	
	private String byteArrayToString(byte[] bArray)
	{// A n'utiliser qu'avec des Strings d�crypt�s!!!
		return new String(bArray);
	}
	
	private byte[] stringToByteArray( String theString)
	{
		return theString.getBytes(); 
	}
	
}
