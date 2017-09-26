package org.sunbird.common.models.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/*
 * @author Amit Kumar 
 * 
 * this class is used for reading properties file
 */
public class PropertiesCache {

  private static final String[] fileName = ProjectUtil.FILE_NAME;
  private final Properties configProp = new Properties();
  public final Map<String, Float> attributePercentageMap = new ConcurrentHashMap<>();

  /**
   * private default constructor
   */
  private PropertiesCache() {
    for (String file : fileName) {
      InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
      try {
        configProp.load(in);
      } catch (IOException e) {
        ProjectLogger.log("Error in properties cache", e);
      }
    }
    loadWeighted();
  }

  private static class LazyHolder {
    private static final PropertiesCache INSTANCE = new PropertiesCache();
  }

  public static PropertiesCache getInstance() {
    return LazyHolder.INSTANCE;
  }

  public String getProperty(String key) {
    return configProp.getProperty(key) != null ? configProp.getProperty(key) : key;
  }


  private void loadWeighted() {
    String key = configProp.getProperty("user.profile.attribute");
    String value = configProp.getProperty("user.profile.weighted");
    if (ProjectUtil.isStringNullOREmpty(key)) {
      ProjectLogger.log("Profile completeness value is not set==", LoggerEnum.INFO.name());
    } else {
      String keys[] = key.split(",");
      String values[] = value.split(",");
      if (keys.length == value.length()) {
        // then take the value from user
        ProjectLogger.log("weighted value is provided by user.");
        for (int i = 0; i < keys.length; i++)
          attributePercentageMap.put(keys[i], new Float(values[i]));
      } else {
        // equally divide all the provided field.
        ProjectLogger.log("weighted value is not provided  by user.");
        float perc = (float) 100.0 / keys.length;
        for (int i = 0; i < keys.length; i++)
          attributePercentageMap.put(keys[i], perc);
      }
    }
  }
}
