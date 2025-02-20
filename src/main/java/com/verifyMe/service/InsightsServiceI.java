package com.verifyMe.service;

import java.util.Map;

public interface InsightsServiceI {

	Map<String, Integer> getInsights();

	Map<String, Map<String, Integer>> getInsightsForUser(String username);

}
