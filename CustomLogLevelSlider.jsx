// Frontend Plugin - LogLevelSlider Component Extension
// File: src/components/LogLevelSlider/CustomLogLevelSlider.jsx

import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames/bind';
import { connect } from 'react-redux';
import { injectIntl, defineMessages } from 'react-intl';
import { InputSlider } from '@reportportal/ui-kit';
import styles from './customLogLevelSlider.scss';

const cx = classNames.bind(styles);

// Extended log levels including custom ones
const EXTENDED_LOG_LEVELS = {
  OFF: 60000,
  FATAL: 50000,
  ERROR: 40000,
  WARN: 30000,
  INFO: 20000,
  DEBUG: 10000,
  TRACE: 5000,
  // Custom log levels
  VERBOSE: 3000,
  FINEST: 1000,
  ALL: 0
};

const LEVEL_ORDER = [
  'ALL',
  'FINEST', 
  'VERBOSE',
  'TRACE',
  'DEBUG',
  'INFO',
  'WARN',
  'ERROR',
  'FATAL',
  'OFF'
];

const messages = defineMessages({
  logLevel: {
    id: 'CustomLogLevelSlider.logLevel',
    defaultMessage: 'Log level'
  },
  all: { id: 'LogLevel.all', defaultMessage: 'All' },
  finest: { id: 'LogLevel.finest', defaultMessage: 'Finest' },
  verbose: { id: 'LogLevel.verbose', defaultMessage: 'Verbose' },
  trace: { id: 'LogLevel.trace', defaultMessage: 'Trace' },
  debug: { id: 'LogLevel.debug', defaultMessage: 'Debug' },
  info: { id: 'LogLevel.info', defaultMessage: 'Info' },
  warn: { id: 'LogLevel.warn', defaultMessage: 'Warn' },
  error: { id: 'LogLevel.error', defaultMessage: 'Error' },
  fatal: { id: 'LogLevel.fatal', defaultMessage: 'Fatal' },
  off: { id: 'LogLevel.off', defaultMessage: 'Off' }
});

const CustomLogLevelSlider = ({ 
  intl, 
  logLevel, 
  onChange,
  disabled = false,
  className = '' 
}) => {
  const [currentLevel, setCurrentLevel] = useState(logLevel || 'INFO');

  useEffect(() => {
    setCurrentLevel(logLevel || 'INFO');
  }, [logLevel]);

  const getLevelIndex = (level) => LEVEL_ORDER.indexOf(level);
  const getLevelByIndex = (index) => LEVEL_ORDER[index];

  const handleSliderChange = (value) => {
    const newLevel = getLevelByIndex(value);
    setCurrentLevel(newLevel);
    if (onChange) {
      onChange(newLevel);
    }
  };

  const getLevelColor = (level) => {
    const colors = {
      OFF: '#8c8c8c',
      FATAL: '#d32f2f',
      ERROR: '#f44336',
      WARN: '#ff9800',
      INFO: '#2196f3',
      DEBUG: '#4caf50',
      TRACE: '#9c27b0',
      VERBOSE: '#673ab7',
      FINEST: '#3f51b5',
      ALL: '#607d8b'
    };
    return colors[level] || '#2196f3';
  };

  const currentIndex = getLevelIndex(currentLevel);
  const maxIndex = LEVEL_ORDER.length - 1;

  return (
    <div className={cx('custom-log-level-slider', className)}>
      <div className={cx('slider-header')}>
        <span className={cx('label')}>
          {intl.formatMessage(messages.logLevel)}
        </span>
        <span 
          className={cx('current-level')}
          style={{ color: getLevelColor(currentLevel) }}
        >
          {intl.formatMessage(messages[currentLevel.toLowerCase()])}
        </span>
      </div>
      
      <div className={cx('slider-container')}>
        <InputSlider
          value={currentIndex}
          min={0}
          max={maxIndex}
          step={1}
          onChange={handleSliderChange}
          disabled={disabled}
          marks={LEVEL_ORDER.reduce((acc, level, index) => {
            acc[index] = {
              label: intl.formatMessage(messages[level.toLowerCase()]),
              style: { color: getLevelColor(level) }
            };
            return acc;
          }, {})}
        />
      </div>
      
      <div className={cx('level-indicators')}>
        {LEVEL_ORDER.map((level, index) => (
          <div
            key={level}
            className={cx('level-indicator', {
              active: index <= currentIndex
            })}
            style={{ 
              backgroundColor: index <= currentIndex ? getLevelColor(level) : '#e0e0e0' 
            }}
          />
        ))}
      </div>
    </div>
  );
};

CustomLogLevelSlider.propTypes = {
  intl: PropTypes.object.isRequired,
  logLevel: PropTypes.string,
  onChange: PropTypes.func,
  disabled: PropTypes.bool,
  className: PropTypes.string
};

export default injectIntl(CustomLogLevelSlider);
