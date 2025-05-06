package com.nexigroup.pagopa.cruscotto.service.validation;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class FutureOrPresentValidator implements ConstraintValidator<FutureOrPresent, Object> {

    private final Logger logger = LoggerFactory.getLogger(FutureOrPresentValidator.class);

    private String date;
    private String pattern;
    private boolean present;

    @Override
    public void initialize(FutureOrPresent constraintAnnotation) {
        date = constraintAnnotation.date();
        pattern = constraintAnnotation.pattern();
        present = constraintAnnotation.present();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        
    	boolean result = true;
    	
    	try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            String strDate = BeanUtils.getProperty(value, date);

            if(strDate != null) {
                Date dateToCheck = sdf.parse(strDate);
                Date currentDate = new Date();
                result = dateToCheck.after(currentDate);
                
                if(!result && present) {
                	result = dateToCheck.equals(currentDate);
                }                
            }
            return result;
        }
        catch(ParseException parseException) {
            logger.error("Problemi formattazione date");

            return true;
        }  catch(Exception exception) {
            logger.error("Problemi validazione date");

            return true;
        }
    }
}
