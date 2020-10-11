package sf.codingcompetition2020;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.Integer;
import java.lang.Float;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.validation.Schema;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import sf.codingcompetition2020.structures.Agent;
import sf.codingcompetition2020.structures.Claim;
import sf.codingcompetition2020.structures.Customer;
import sf.codingcompetition2020.structures.Vendor;

public class CodingCompCsvUtil {
	CsvMapper csvMapper = new CsvMapper();
	CsvSchema schema = CsvSchema.emptySchema().withHeader();
	
	public CodingCompCsvUtil() {
		super();
	}

	/* #1 
	 * readCsvFile() -- Read in a CSV File and return a list of entries in that file.
	 * @param filePath -- Path to file being read in.
	 * @param classType -- Class of entries being read in.
	 * @return -- List of entries being returned.
	 */
	public <T> List<T> readCsvFile(String filePath, Class<T> classType) {
		ObjectReader objReader = csvMapper.reader(classType).with(schema);
		
		List<T> list = new ArrayList<>();
		
		try (Reader reader = new FileReader(filePath)) {
			MappingIterator<T> mi = objReader.readValues(reader);
			
		    while (mi.hasNext()) {
		    	list.add(mi.next());
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
	/* #2
	 * getAgentCountInArea() -- Return the number of agents in a given area.
	 * @param filePath -- Path to file being read in.
	 * @param area -- The area from which the agents should be counted.
	 * @return -- The number of agents in a given area
	 */
	public int getAgentCountInArea(String filePath,String area) {
		List<Agent> agentList = readCsvFile(filePath, Agent.class);
		
		int count = 0;
		for (Agent agent: agentList) {
			if (agent.getArea().equals(area)) {
				count ++;
			}
		}
		
		return count;
	}

	
	/* #3
	 * getAgentsInAreaThatSpeakLanguage() -- Return a list of agents from a given area, that speak a certain language.
	 * @param filePath -- Path to file being read in.
	 * @param area -- The area from which the agents should be counted.
	 * @param language -- The language spoken by the agent(s).
	 * @return -- The number of agents in a given area
	 */
	public List<Agent> getAgentsInAreaThatSpeakLanguage(String filePath, String area, String language) {
		List<Agent> agentList = readCsvFile(filePath, Agent.class);
		List<Agent> agentsInAreaThatSpeakLanguage = new ArrayList<>();
		
		for (Agent agent: agentList) {
			if (agent.getArea().equals(area) && agent.getLanguage().equals(language)) {
				agentsInAreaThatSpeakLanguage.add(agent);
			}
		}
		
		return agentsInAreaThatSpeakLanguage;
	}
	
	
	/* #4
	 * countCustomersFromAreaThatUseAgent() -- Return the number of individuals from an area that use a certain agent.
	 * @param filePath -- Path to file being read in.
	 * @param customerArea -- The area from which the customers should be counted.
	 * @param agentFirstName -- First name of agent.
	 * @param agentLastName -- Last name of agent.
	 * @return -- The number of customers that use a certain agent in a given area.
	 */
	public short countCustomersFromAreaThatUseAgent(Map<String,String> csvFilePaths, String customerArea, String agentFirstName, String agentLastName) {
		short countCustomers = 0;
		int agentId = -1;
		List<Agent> agentList = readCsvFile(csvFilePaths.get("agentList"), Agent.class);
		
		for (Agent a : agentList) {
			if (a.getFirstName().equals(agentFirstName) && a.getLastName().equals(agentLastName)) {
				agentId = a.getAgentId();
				break;
			}
		}
		
		List<Customer> customerList = readCsvFile(csvFilePaths.get("customerList"), Customer.class);
		for (Customer c : customerList) {
			if (c.getArea().equals(customerArea) && c.getAgentId() == agentId) {
				countCustomers++;
			}
		}
		return countCustomers;
	}
	
	/* #5
	 * getCustomersRetainedForYearsByPlcyCostAsc() -- Return a list of customers retained for a given number of years, in ascending order of their policy cost.
	 * @param filePath -- Path to file being read in.
	 * @param yearsOfServeice -- Number of years the person has been a customer.
	 * @return -- List of customers retained for a given number of years, in ascending order of policy cost.
	 */
	public List<Customer> getCustomersRetainedForYearsByPlcyCostAsc(String customerFilePath, short yearsOfService) {
		List<Customer> customerList = readCsvFile(customerFilePath, Customer.class);
		List<Customer> yearsList = new ArrayList();
		
		for (Customer c : customerList) {
			if (c.getYearsOfService() == yearsOfService) {
				yearsList.add(c);
			}
		}
		Collections.sort(yearsList, (Customer c1, Customer c2) ->{
			return c1.getTotalMonthlyPremium().compareToIgnoreCase(c2.getTotalMonthlyPremium());
		});
		
		return yearsList;
		
	}

	
	/* #6
	 * getLeadsForInsurance() -- Return a list of individuals who’ve made an inquiry for a policy but have not signed up.
	 * *HINT* -- Look for customers that currently have no policies with the insurance company.
	 * @param filePath -- Path to file being read in.
	 * @return -- List of customers who’ve made an inquiry for a policy but have not signed up.
	 */
	public List<Customer> getLeadsForInsurance(String filePath) {
		List<Customer> customerList = readCsvFile(filePath, Customer.class);
		List<Customer> leadList = new ArrayList<>();
		
		for (Customer customer: customerList) {
			if (!customer.isAutoPolicy() && !customer.isHomePolicy() && !customer.isRentersPolicy()) {
				leadList.add(customer);
			}
		}
		
		return leadList;
	}


	/* #7
	 * getVendorsWithGivenRatingThatAreInScope() -- Return a list of vendors within an area and include options to narrow it down by: 
			a.	Vendor rating
			b.	Whether that vendor is in scope of the insurance (if inScope == false, return all vendors in OR out of scope, if inScope == true, return ONLY vendors in scope)
	 * @param filePath -- Path to file being read in.
	 * @param area -- Area of the vendor.
	 * @param inScope -- Whether or not the vendor is in scope of the insurance.
	 * @param vendorRating -- The rating of the vendor.
	 * @return -- List of vendors within a given area, filtered by scope and vendor rating.
	 */
	public List<Vendor> getVendorsWithGivenRatingThatAreInScope(String filePath, String area, boolean inScope, int vendorRating) {
		List<Vendor> vendorList = readCsvFile(filePath, Vendor.class);
		List<Vendor> vendorsWithGivenRatingInScope = new ArrayList<>();
		
		for (Vendor vendor: vendorList) {
			if (vendor.getArea().equals(area) && vendor.getVendorRating() >= vendorRating) {
				if (inScope) {
					if (vendor.isInScope()) {
						vendorsWithGivenRatingInScope.add(vendor);
					}
				} else {
					vendorsWithGivenRatingInScope.add(vendor);
				}
			}
		}
		
		return vendorsWithGivenRatingInScope;
	}


	/* #8
	 * getUndisclosedDrivers() -- Return a list of customers between the age of 40 and 50 years (inclusive), who have:
			a.	More than X cars
			b.	less than or equal to X number of dependents.
	 * @param filePath -- Path to file being read in.
	 * @param vehiclesInsured -- The number of vehicles insured.
	 * @param dependents -- The number of dependents on the insurance policy.
	 * @return -- List of customers filtered by age, number of vehicles insured and the number of dependents.
	 */
	public List<Customer> getUndisclosedDrivers(String filePath, int vehiclesInsured, int dependents) {
		List<Customer> customerList = readCsvFile(filePath, Customer.class);
		List<Customer> undisclosedDrivers = new ArrayList<>();
		
		for (Customer customer: customerList) {
			if (customer.getAge() >= 40 
					&& customer.getAge() <= 50 
					&& customer.getVehiclesInsured() > vehiclesInsured
					&& customer.getDependents().size() <= dependents) {
				undisclosedDrivers.add(customer);
			}
		}
		
		return undisclosedDrivers;	
	}	


	/* #9
	 * getAgentIdGivenRank() -- Return the agent with the given rank based on average customer satisfaction rating. 
	 * *HINT* -- Rating is calculated by taking all the agent rating by customers (1-5 scale) and dividing by the total number 
	 * of reviews for the agent.
	 * @param filePath -- Path to file being read in.
	 * @param agentRank -- The rank of the agent being requested.
	 * @return -- Agent ID of agent with the given rank.
	 */
	public int getAgentIdGivenRank(String filePath, int agentRank) {
		List<Customer> customerList = readCsvFile(filePath, Customer.class);
		
		Map<Integer, Float> sumOfAgentRatings = new HashMap<>();
		Map<Integer, Integer> countOfAgentRatings = new HashMap<>();
		
		for (Customer customer: customerList) {
			Integer agentId = customer.getAgentId();
			Float agentRating = (float) customer.getAgentRating();
			Float currentSumOfAgentRatings = sumOfAgentRatings.get(agentId) != null ? sumOfAgentRatings.get(agentId) : 0;
			Integer currentCountOfAgentRatings = countOfAgentRatings.get(agentId) != null ? countOfAgentRatings.get(agentId) : 0;
			
			Float newSumOfAgentRatings = currentSumOfAgentRatings + agentRating;
			Integer newCountOfAgentRatings = currentCountOfAgentRatings + 1;
			
			sumOfAgentRatings.put(agentId, newSumOfAgentRatings);
			countOfAgentRatings.put(agentId, newCountOfAgentRatings);
		}
		
		Map<Integer, Float> averageAgentRating = new HashMap<>();
		for (Integer agentId: sumOfAgentRatings.keySet()) {
			averageAgentRating.put(agentId, sumOfAgentRatings.get(agentId) / countOfAgentRatings.get(agentId));
		}
		
		List<Integer> sortedAgentIdsByRating = averageAgentRating.entrySet().stream()
				.sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		
		return sortedAgentIdsByRating.get(agentRank);
	}	

	
	/* #10
	 * getCustomersWithClaims() -- Return a list of customers who’ve filed a claim within the last <numberOfMonths> (inclusive). 
	 * @param filePath -- Path to file being read in.
	 * @param monthsOpen -- Number of months a policy has been open.
	 * @return -- List of customers who’ve filed a claim within the last <numberOfMonths>.
	 */
	public List<Customer> getCustomersWithClaims(Map<String,String> csvFilePaths, short monthsOpen) {
		Map<Integer, Boolean> ids = new HashMap<>();
		
		List<Customer> customerList = readCsvFile(csvFilePaths.get("customerList"), Customer.class);
		List<Claim> claimList = readCsvFile(csvFilePaths.get("claimList"), Claim.class);
		List<Customer> customersWithClaim = new ArrayList();
		
		for (Claim claim: claimList) {
			if (claim.getMonthsOpen() <= monthsOpen) {
				if (ids.get(claim.getCustomerId()) == null) {
					ids.put(claim.getCustomerId(), true);
					Customer c = customerList.get(claim.getCustomerId() - 1);
					customersWithClaim.add(c);
				}
				
			}
		}
		
		return customersWithClaim;
	}	

}
