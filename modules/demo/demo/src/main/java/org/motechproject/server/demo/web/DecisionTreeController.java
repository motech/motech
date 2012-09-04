package org.motechproject.server.demo.web;

import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.TextToSpeechPrompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.server.service.DecisionTreeService;
import org.motechproject.server.demo.model.TreeRecord;
import org.motechproject.server.demo.service.TreeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
public class DecisionTreeController {
    @Autowired
    private DecisionTreeService decisionTreeService;

    @RequestMapping(value = "/trees", method = RequestMethod.GET)
    @ResponseBody
    public List<Tree> getDecisionTrees() {
        return decisionTreeService.getDecisionTrees();
    }

    @RequestMapping(value = "/trees/{treeId}", method = RequestMethod.GET)
    @ResponseBody
    public TreeRecord getTree(@PathVariable String treeId) {
        return TreeConverter.convertToTreeRecord(decisionTreeService.getDecisionTree(treeId));
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/trees/{treeId}/remove", method = RequestMethod.POST)
    public void removeTree(@PathVariable final String treeId) {
        decisionTreeService.deleteDecisionTree(treeId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/trees/create", method = RequestMethod.POST)
    public void saveTree(@RequestBody TreeRecord treeRecord) throws Exception {
        decisionTreeService.saveDecisionTree(TreeConverter.convertToTree(treeRecord));
    }

    @RequestMapping(value = "/trees/cycle", method = RequestMethod.POST)
    @ResponseBody
    public boolean findTreeCycle(@RequestBody TreeRecord treeRecord) throws Exception {
        return treeRecord.isCyclic();
    }

    @RequestMapping(value = "/tree/example", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createExampleTree() {
        decisionTreeService.saveDecisionTree(new Tree()
                .setName("Illness Tree")
                .setRootTransition(new Transition().setDestinationNode(new Node()
                        .setPrompts(new TextToSpeechPrompt().setMessage("if you feel sick select 1, if not select 2"))
                        .setTransitions(new Object[][]{
                                {"1", new Transition().setName("pressed1")
                                        .setDestinationNode(new Node()
                                                .setPrompts(new TextToSpeechPrompt().setMessage("if you have a fever select 1, if not select 3"))
                                                .setTransitions(new Object[][]{
                                                        {"1", new Transition().setName("pressed1").setDestinationNode(
                                                                new Node().setPrompts(new TextToSpeechPrompt().setMessage("come to the hospital now"))
                                                        )},
                                                        {"3", new Transition().setName("pressed3").setDestinationNode(
                                                                new Node().setPrompts(new TextToSpeechPrompt().setMessage("be patient, we will call you"))
                                                        )}
                                                })
                                        )},
                                {"2", new Transition().setName("pressed2")
                                        .setDestinationNode(new Node().setPrompts(new TextToSpeechPrompt().setMessage("Check with us again")))}
                        })
                )));
    }


}
