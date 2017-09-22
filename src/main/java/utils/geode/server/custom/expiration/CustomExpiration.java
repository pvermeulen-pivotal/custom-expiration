package utils.geode.server.custom.expiration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.ExpirationAction;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.Region.Entry;
import org.apache.geode.pdx.FieldType;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.internal.PdxInstanceImpl;

public class CustomExpiration implements CustomExpiry, Declarable {

	// Java runtime property
	// -D${region-name}ExpirationFields=fieldName,fieldValue,timeToExpire,expirationAction;fieldName,fieldValue,timeToExpire,expirationAction;...;...;

	private List<Expiration> expirationList = new ArrayList<Expiration>();
	private Log log = LogFactory.getLog(CustomExpiration.class);
	private boolean customRegionExpirationInitialized = false;

	public void close() {
	}

	public ExpirationAttributes getExpiry(Entry entry) {
		if (!customRegionExpirationInitialized)
			init(entry.getRegion().getName());
		if (expirationList.size() > 0) {
			if (entry.getValue() instanceof PdxInstance) {
				PdxInstanceImpl pdxInstance = (PdxInstanceImpl) entry.getValue();
				return checkEntry(pdxInstance);
			} else {
				log.info("Entry is not a PDX instance in region " + entry.getRegion().getName());
			}
		}
		return null;
	}

	private ExpirationAttributes checkEntry(PdxInstanceImpl pdx) {
		for (Expiration ex : expirationList) {
			ExpirationAttributes ea = checkForExpiration(ex, pdx);
			if (ea != null)
				return ea;
		}
		return null;
	}

	private ExpirationAttributes checkForExpiration(Expiration ex, PdxInstanceImpl pdx) {
		if (pdx.getPdxField(ex.getField()).getFieldType() == FieldType.BOOLEAN) {
			boolean b = Boolean.valueOf(ex.getValue());
			if (((Boolean) pdx.getField(ex.getField())).equals(b))
				return new ExpirationAttributes(ex.getSeconds(), ex.getAction());
		} else if (pdx.getPdxField(ex.getField()).getFieldType() == FieldType.BYTE) {
			byte b = Byte.valueOf(ex.getValue());
			if (((Byte) pdx.getField(ex.getField())).equals(b))
				return new ExpirationAttributes(ex.getSeconds(), ex.getAction());
		} else if (pdx.getPdxField(ex.getField()).getFieldType() == FieldType.CHAR) {
			if (((Character) pdx.getField(ex.getField())).toString().equals(ex.getValue()))
				return new ExpirationAttributes(ex.getSeconds(), ex.getAction());
		} else if (pdx.getPdxField(ex.getField()).getFieldType() == FieldType.DOUBLE) {
			Double b = Double.parseDouble(ex.getValue());
			if (((Double) pdx.getField(ex.getField())).equals(b))
				return new ExpirationAttributes(ex.getSeconds(), ex.getAction());
		} else if (pdx.getPdxField(ex.getField()).getFieldType() == FieldType.FLOAT) {
			Float b = Float.parseFloat(ex.getValue());
			if (((Float) pdx.getField(ex.getField())).equals(b))
				return new ExpirationAttributes(ex.getSeconds(), ex.getAction());
		} else if (pdx.getPdxField(ex.getField()).getFieldType() == FieldType.INT) {
			Integer b = Integer.valueOf(ex.getValue());
			if (((Integer) pdx.getField(ex.getField())).equals(b))
				return new ExpirationAttributes(ex.getSeconds(), ex.getAction());
		} else if (pdx.getPdxField(ex.getField()).getFieldType() == FieldType.LONG) {
			Long b = Long.parseLong(ex.getValue());
			if (((Long) pdx.getField(ex.getField())).equals(b))
				return new ExpirationAttributes(ex.getSeconds(), ex.getAction());
		} else if (pdx.getPdxField(ex.getField()).getFieldType() == FieldType.SHORT) {
			Short b = Short.valueOf(ex.getValue());
			if (((Short) pdx.getField(ex.getField())).equals(b))
				return new ExpirationAttributes(ex.getSeconds(), ex.getAction());
		} else if (pdx.getPdxField(ex.getField()).getFieldType() == FieldType.STRING) {
			if (((String) pdx.getField(ex.getField())).equals(ex.getValue()))
				return new ExpirationAttributes(ex.getSeconds(), ex.getAction());
		} else {
			log.info("The PDX field " + pdx.getPdxField(ex.getField()).getFieldName()
					+ " is not a supported type for custom expiration");
		}
		return null;
	}

	private void init(String regionName) {
		log.info("Initializing custom expiration fields");
		customRegionExpirationInitialized = true;
		String fields = System.getProperty(regionName + "ExpirationFields");
		if (fields == null) {
			log.error("No " + regionName + "ExpirationFields property found. Unable to perform custom expiration.");
			return;
		}
		String[] array = fields.split(";");
		if (array == null) {
			log.error("Unable to parse " + regionName + "ExpirationFields property");
			return;
		}
		for (String str : array) {
			String[] expire = str.split(",");
			if (expire != null && expire.length == 4) {
				try {
					ExpirationAction action;
					int seconds = Integer.parseInt(expire[2]);
					if ("DESTROY".equalsIgnoreCase(expire[3])) {
						action = ExpirationAction.DESTROY;
					} else if ("INVALIDATE".equalsIgnoreCase(expire[3])) {
						action = ExpirationAction.INVALIDATE;
					} else if ("LOCAL_DESTROY".equalsIgnoreCase(expire[3])) {
						action = ExpirationAction.LOCAL_DESTROY;
					} else if ("LOCAL_INVALIDATE".equalsIgnoreCase(expire[3])) {
						action = ExpirationAction.LOCAL_INVALIDATE;
					} else {
						action = ExpirationAction.DESTROY;
					}
					expirationList.add(new Expiration(expire[0], expire[1], seconds, action));
				} catch (NumberFormatException e) {
					log.info(regionName + "ExpirationFields Unable to parse number of seconds for field " + expire[0]);
				}
			} else {
				log.info(regionName + "ExpirationFields property Unable to parse expiration field " + str);
			}
		}
	}
}