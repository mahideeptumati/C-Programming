package com.rail.assist.aspect;

import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.rail.assist.config.CurrentUser;
import com.rail.assist.entities.UserDetail;


public class ASSISTLoggingAspect {

	@Autowired
	private CurrentUser currentUser;
	
	private static final String LOGFILENAME = "assistweblogname";

	public void logBefore(JoinPoint joinPoint) {
		Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
		if(currentUser.getCurrentUserForLogging()!=null) {

			UserDetail user = (UserDetail)currentUser.getCurrentUserForLogging();
			ThreadContext.put(LOGFILENAME, user.getUserFirstName()+"-"+user.getUserLastName());
		} else {
			if (joinPoint.getSignature() instanceof MethodSignature) {
				String methodName = joinPoint.getSignature().getName();
				if("processUserLogin".equals(methodName)) {

					Object[] methodargs = joinPoint.getArgs();
					for(Object methodArg : methodargs) {
						if(methodArg instanceof UserDetail) {
							Thread.currentThread().setName(((UserDetail)methodArg).getUserFirstName());
							break;
						}
					}
					
				}
			}

		}

		logger.info("Beginning of the method {}", joinPoint.getSignature().getName());

		if(logger.isDebugEnabled()) {

			if (joinPoint.getSignature() instanceof MethodSignature) {
				MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();

				String[] paramNames = methodSignature.getParameterNames();

				Object[] methodargs = joinPoint.getArgs();
				
				if(paramNames!=null) {
					for(int i=0;i<paramNames.length; i++) {
						logger.debug("The parameter number is {} and name is {} and value is {}",i, paramNames[i], methodargs[i]);
					}
				}
				
			}
		}

		ThreadContext.remove(LOGFILENAME);

	}

	public void logAfter(JoinPoint joinPoint, Object retVal) {

		Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());

		if(currentUser.getCurrentUserForLogging()!=null) {

			UserDetail user = (UserDetail)currentUser.getCurrentUserForLogging();
			ThreadContext.put(LOGFILENAME, user.getUserFirstName()+"-"+user.getUserLastName());
		}

		logger.info("End of the method {}", joinPoint.getSignature().getName());

		logger.debug("The result of the method is "+retVal);

		ThreadContext.remove(LOGFILENAME);
	}


	public void logException(JoinPoint joinPoint, Throwable error) {

		Logger logger = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());

		if(currentUser.getCurrentUserForLogging()!=null) {
			UserDetail user = (UserDetail)currentUser.getCurrentUserForLogging();
			ThreadContext.put(LOGFILENAME, user.getUserFirstName()+"-"+user.getUserLastName());
		}
		
		logger.error("Exception is ", error);

		ThreadContext.remove(LOGFILENAME);
	}
 
}
