package org.motechproject.server.decisiontree.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DecisionTreeServiceTest {

    @InjectMocks
    DecisionTreeService decisionTreeService = new DecisionTreeServiceImpl();



    @Before
    public void initMocks() {

        MockitoAnnotations.initMocks(this);
     }

     //TODO - implement
    @Test
    public void getNodeTest () {
//        decisionTreeService.getNode("", "");

    }

     //TODO - implement
    @Test
    public void getTransitionNodeTest () {

//        decisionTreeService.getNode("", "");

    }
}
