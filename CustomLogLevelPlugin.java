// Backend Plugin Extension
// File: src/main/java/com/epam/reportportal/extension/CustomLogLevelPlugin.java

package com.epam.reportportal.extension;

import com.epam.reportportal.extension.common.IntegrationTypeProperties;
import com.epam.reportportal.extension.common.ReportPortalExtensionPoint;
import com.epam.ta.reportportal.commons.validation.BusinessRule;
import com.epam.ta.reportportal.entity.enums.LogLevel;
import com.epam.ta.reportportal.entity.item.TestItem;
import com.epam.ta.reportportal.entity.log.Log;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CustomLogLevelPlugin implements ReportPortalExtensionPoint {
    
    public static final String PLUGIN_ID = "customLogLevels";
    
    // Extended log level mappings
    private static final Map<String, Integer> EXTENDED_LOG_LEVELS = new HashMap<>();
    
    static {
        EXTENDED_LOG_LEVELS.put("OFF", 60000);
        EXTENDED_LOG_LEVELS.put("FATAL", 50000);
        EXTENDED_LOG_LEVELS.put("ERROR", 40000);
        EXTENDED_LOG_LEVELS.put("WARN", 30000);
        EXTENDED_LOG_LEVELS.put("INFO", 20000);
        EXTENDED_LOG_LEVELS.put("DEBUG", 10000);
        EXTENDED_LOG_LEVELS.put("TRACE", 5000);
        EXTENDED_LOG_LEVELS.put("VERBOSE", 3000);
        EXTENDED_LOG_LEVELS.put("FINEST", 1000);
        EXTENDED_LOG_LEVELS.put("ALL", 0);
    }
    
    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }
    
    @Override
    public IntegrationTypeProperties getIntegrationTypeProperties() {
        return IntegrationTypeProperties.builder()
                .withName("Custom Log Levels")
                .withVersion("1.0.0")
                .withDescription("Extended log level support with custom levels")
                .build();
    }
    
    /**
     * Filter logs based on extended log level
     */
    public List<Log> filterLogsByLevel(List<Log> logs, String minLevel) {
        if (minLevel == null || !EXTENDED_LOG_LEVELS.containsKey(minLevel.toUpperCase())) {
            return logs;
        }
        
        int minLevelValue = EXTENDED_LOG_LEVELS.get(minLevel.toUpperCase());
        
        return logs.stream()
                .filter(log -> {
                    String logLevel = log.getLogLevel() != null ? 
                        log.getLogLevel().name() : "INFO";
                    Integer logLevelValue = EXTENDED_LOG_LEVELS.get(logLevel.toUpperCase());
                    return logLevelValue != null && logLevelValue >= minLevelValue;
                })
                .collect(ArrayList::new, (list, log) -> list.add(log), ArrayList::addAll);
    }
    
    /**
     * Validate custom log level
     */
    public void validateLogLevel(String logLevel) {
        BusinessRule.expect(EXTENDED_LOG_LEVELS.containsKey(logLevel.toUpperCase()), 
            equalTo(true))
            .verify(ErrorType.INCORRECT_REQUEST, 
                "Unsupported log level: " + logLevel + ". Supported levels: " + 
                String.join(", ", EXTENDED_LOG_LEVELS.keySet()));
    }
    
    /**
     * Get all supported log levels
     */
    public Set<String> getSupportedLogLevels() {
        return new HashSet<>(EXTENDED_LOG_LEVELS.keySet());
    }
    
    /**
     * Convert string log level to numeric value
     */
    public Integer getLogLevelValue(String logLevel) {
        return EXTENDED_LOG_LEVELS.get(logLevel.toUpperCase());
    }
}

// CSS Styles
// File: src/components/LogLevelSlider/customLogLevelSlider.scss

.custom-log-level-slider {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  
  .slider-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    
    .label {
      font-weight: 600;
      color: #495057;
      font-size: 14px;
    }
    
    .current-level {
      font-weight: 700;
      font-size: 16px;
      padding: 4px 12px;
      border-radius: 16px;
      background-color: rgba(255, 255, 255, 0.8);
      border: 1px solid currentColor;
    }
  }
  
  .slider-container {
    margin: 16px 0;
    
    .ant-slider {
      .ant-slider-rail {
        background: linear-gradient(
          to right,
          #607d8b 0%,
          #3f51b5 10%,
          #673ab7 20%,
          #9c27b0 30%,
          #4caf50 40%,
          #2196f3 50%,
          #ff9800 60%,
          #f44336 80%,
          #d32f2f 90%,
          #8c8c8c 100%
        );
        height: 6px;
      }
      
      .ant-slider-track {
        background: transparent;
      }
      
      .ant-slider-handle {
        width: 20px;
        height: 20px;
        border: 3px solid #fff;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
        
        &:hover, &:focus {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
        }
      }
    }
  }
  
  .level-indicators {
    display: flex;
    justify-content: space-between;
    gap: 2px;
    margin-top: 8px;
    
    .level-indicator {
      flex: 1;
      height: 4px;
      border-radius: 2px;
      transition: all 0.3s ease;
      
      &.active {
        transform: scaleY(1.5);
      }
    }
  }
  
  &.disabled {
    opacity: 0.6;
    pointer-events: none;
  }
}

// Plugin Registration
// File: plugin.json
{
  "id": "customLogLevels",
  "name": "Custom Log Levels",
  "version": "1.0.0",
  "description": "Extended log level support with additional custom levels",
  "main": "dist/index.js",
  "author": "Your Name",
  "license": "MIT",
  "reportPortalVersion": ">=5.0.0",
  "extensions": {
    "ui": {
      "components": {
        "logLevelSlider": "./src/components/LogLevelSlider/CustomLogLevelSlider"
      }
    },
    "api": {
      "endpoints": [
        {
          "path": "/plugin/customLogLevels/levels",
          "method": "GET",
          "handler": "getSupportedLogLevels"
        }
      ]
    }
  }
}
