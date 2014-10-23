package org.survey.user.bean;

import java.util.ArrayList;

import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.survey.poll.model.Poll;
import org.survey.poll.model.Question;
import org.survey.poll.model.QuestionFactory;
import org.survey.poll.model.QuestionType;
import org.survey.poll.service.PollService;
import org.survey.user.FacesUtil;

@Component
@ViewScoped
@Slf4j
@SuppressWarnings("PMD.UnusedPrivateField")
public class EditPollBean {
    // TODO: change to id
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private Poll poll;
    @Setter
    @Autowired
    PollService pollService;
    @SuppressWarnings("pmd.UnusedPrivateField")
    @Getter
    private QuestionType[] questionTypes = QuestionType.values();

    public String addPoll() {
        poll = new Poll();
        return "editPoll";
    }

    /**
     * Called from polls.xhtml when user pressed Edit in pollTable.
     */
    public String editPoll() {
        name = getRequestParameter("name");
        if (name != null) {
            poll = pollService.findOne(name);
        }
        return "editPoll";
    }

    /**
     * Called from editPoll.xhtml page when user presses Add question button.
     */
    public void addQuestion() {
        if (poll.getQuestions() == null) {
            poll.setQuestions(new ArrayList<Question>());
        }
        Question question = new Question();
        question.setPoll(poll);
        poll.getQuestions().add(question);
    }

    /**
     * Called from editPoll.xhtml page when user changes a question type.
     * editPoll.xhtml contains a selectOneMenu with
     * valueChangeListener="#{editPollBean.questionTypeChanged(status.index)}"
     */
    public void questionTypeChanged(int index) {
        log.debug("index: {}", index);
        Question question = poll.getQuestions().get(index);
        log.debug("oldQuestion.type: {}", question.getType());
        QuestionType questionType = QuestionType.valueOf(QuestionType.class, question.getType());
        log.debug("questionType: {}", questionType);
        Question newQuestion = QuestionFactory.createQuestionFrom(question,
                questionType, poll);
        poll.getQuestions().set(index, newQuestion);
    }

    /**
     * Called from editPoll.xhtml page when user presses Save button.
     */
    public String savePoll() {
        log.info(poll.toString());
        try {
            if (poll.getId() == null) {
                pollService.create(poll);
            } else {
                pollService.update(poll);
            }
        } catch (IllegalArgumentException e) {
            log.debug("Unable to create poll, a poll with same name already exists: {]", poll.getName());
            showMessage(null, "pollExists");
            return null;
        }
        return "pollSaved";
    }

    public void cancel() {
        poll = new Poll();
    }

    protected String getRequestParameter(String parameterName) {
        return FacesUtil.getRequestParameter(parameterName);
    }

    /**
     * showMessage is package private to enable overriding in a test case.
     */
    void showMessage(String id, String messageKey) {
        FacesUtil.showMessage(id, messageKey);
    }
}