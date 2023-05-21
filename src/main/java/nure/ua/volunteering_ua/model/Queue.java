package nure.ua.volunteering_ua.model;

import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.model.user.SocialCategory;
import nure.ua.volunteering_ua.model.user.VolunteeringType;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;

import java.util.Comparator;
import java.util.List;


public class Queue {


    public Pair<Integer, List<Aid_Request>> getQueueNumber(SocialCategory socialCategory, VolunteeringType volunteeringType, List<Aid_Request> requests) {
//        requests.sort(Comparator.comparingInt((Aid_Request request) -> -getPriority(request.getCustomer().getUser().getSocialCategory().toString()))
//                .thenComparingInt(request -> -getPriorityByVolunteeringType(request.getVolunteeringType().toString())));
//
//        int lastQueueNumber = requests.size();
//        int newQueueNumberIndex = lastQueueNumber;
//        for (int i = requests.size() - 1; i >= 0; i--) {
//            Aid_Request request = requests.get(i);
//            if (getPriority(request.getCustomer().getUser().getSocialCategory().toString()) > getPriority(socialCategory.toString())
//                    || (getPriority(request.getCustomer().getUser().getSocialCategory().toString()) == getPriority(socialCategory.toString())
//                    && getPriorityByVolunteeringType(request.getVolunteeringType().toString()) > getPriorityByVolunteeringType(volunteeringType.toString()))) {
//                newQueueNumberIndex = i + 1;
//                break;
//            }
//        }
//
        int newQueueNumber = getQueueNumbe(socialCategory,volunteeringType,requests);
//        if (newQueueNumberIndex < requests.size()) {
//            newQueueNumber = requests.get(newQueueNumberIndex).getQueueNumber();
//            updateQueueNumbers(requests, newQueueNumberIndex, newQueueNumber + 1);
//        } else {
//            newQueueNumber = requests.size() + 1;
//        }
//
//        Aid_Request newRequest = new Aid_Request(); // Create a new instance of the request
//        newRequest.setQueueNumber(newQueueNumber); // Set the queue number for the new request
//        newRequest.setVolunteeringType(volunteeringType); // Set the volunteering type for the new request
//        // Set other properties of the new request as needed
//        requests.add(newRequest); // Add the new request to the list
        updateQueueNumber(requests,newQueueNumber);
        return Pair.of(newQueueNumber, requests);
    }

    private int getQueueNumbe(SocialCategory socialCategory, VolunteeringType volunteeringType, List<Aid_Request> requests){
        requests.sort(Comparator.comparingInt(Aid_Request::getQueueNumber));
        if(requests.isEmpty()){
            return 1;
        }
        for(int i = requests.size()-1; i>=0;){
            Aid_Request request = requests.get(i);
            if(getPriority(request.getCustomer().getUser().getSocialCategory().toString()) > getPriority(socialCategory.toString())){
                i--;
            }else if(getPriority(request.getCustomer().getUser().getSocialCategory().toString()) <= getPriority(socialCategory.toString())
                    && getPriorityByVolunteeringType(request.getVolunteeringType().toString()) > getPriorityByVolunteeringType(volunteeringType.toString())){
                i--;
            }else if(getPriority(request.getCustomer().getUser().getSocialCategory().toString()) <= getPriority(socialCategory.toString())
                    && getPriorityByVolunteeringType(request.getVolunteeringType().toString()) <= getPriorityByVolunteeringType(volunteeringType.toString())){
                return requests.get(i).getQueueNumber()+1;
            }
        }
        throw new CustomException("Error occurred when making queue", HttpStatus.BAD_REQUEST);
    }

    private void updateQueueNumbers(List<Aid_Request> requests, int startIndex, int initialQueueNumber) {
        for (int i = startIndex; i < requests.size(); i++) {
            requests.get(i).setQueueNumber(initialQueueNumber++);
        }
    }

    private void updateQueueNumber(List<Aid_Request> requests, int startIndex) {
        for (int i = startIndex-1; i < requests.size(); i++) {
            int newIndex = requests.get(i).getQueueNumber();
            requests.get(i).setQueueNumber(newIndex+1);
        }
    }

    public int getPriority(String socialStatus) {
        int priority;

        switch (socialStatus) {
            case "ORPHANS_AND_CHILDREN_WITHOUT_PARENTAL_CARE":
                priority = 1;
                break;
            case "REFUGEES_AND_DISPLACED_PERSONS":
                priority = 2;
                break;
            case "VICTIMS_OF_DOMESTIC_VIOLENCE":
                priority = 3;
                break;
            case "PEOPLE_WITH_DISABILITIES":
                priority = 4;
                break;
            case "PENSIONER":
                priority = 5;
                break;
            case "SINGLE_PARENT_FAMILIES":
                priority = 6;
                break;
            case "LARGE_FAMILIES":
                priority = 7;
                break;
            case "LOW_INCOME_FAMILIES":
                priority = 8;
                break;
            case "UNEMPLOYED_PERSONS":
                priority = 9;
                break;
            case "HOMELESS_PERSONS":
                priority = 10;
                break;
            case "NO_CATEGORY":
                priority = 11;
                break;
            default:
                priority = 0; // Default priority if the social status is not recognized
                break;
        }

        return priority;
    }

    public int getPriorityByVolunteeringType(String volunteeringType) {
        int priority;

        switch (volunteeringType) {
            case "SOCIAL":
                priority = 1;
                break;
            case "ANIMALS":
                priority = 2;
                break;
            case "ENVIRONMENTAL":
                priority = 3;
                break;
            default:
                priority = 0; // Default priority if the social status is not recognized
                break;
        }

        return priority;
    }

}
