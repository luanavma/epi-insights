package org.iris.patient.service.ai;

import org.iris.patient.repository.PatientRepository;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(chatMemoryProviderSupplier = RegisterAiService.BeanChatMemoryProviderSupplier.class, tools = {PatientRepository.class})
public interface PatientAIService {

    @SystemMessage("""
        You are a clinical medical assistant specialized in electronic health records (EHR).
        You will receive a patient ID.
        Using the tools available to you (e.g., PatientRepository), fetch the patient's clinical data,
        1. Use the tool `findConditionsByPatient` 'obtain clinical Conditions history' to list clinical Conditions.
        Then, summarize the patient's clinical Conditions history 
        without JSONS on response. 
    """)
    @UserMessage("Summarize the clinical Conditions history for patient ID: {{patientKey}}")
    String conditionsHistory(@V("patientKey") String patientKey);
    

    @SystemMessage("""
    You are a clinical assistant AI specialized in pharmacovigilance.
    Your task is to analyze the current medications of a patient identified by their ID.
    You will:
    1. Use the tool `findMedicationTextByPatient` to list all medications the patient is currently using.
    2. Output a clinical risk analysis to aid decision making.
    without JSONS on response.
    """)
    @UserMessage("""
    Analyze the medication safety risks for the patient with ID: {{patientKey}}.
    Use tools to find current medications before starting your analysis.
    """)
    String analyzeMedicationRisks(@V("patientKey") String patientKey);

    @SystemMessage("""
        You are a helpful and knowledgeable clinical assistant AI. 
        You must answer user questions based only on the provided clinical data of the patient.
        Use the available tools to retrieve relevant patient information.
        Answer concisely and clearly.
    """)
    @UserMessage("""
        Patient ID: {patientKey}
        Question: {question}
        Use tools to gather the patient's clinical data (e.g. previous diagnoses, medications, allergies, exams, etc.).
        1. Use the tool `findMedicationTextByPatient` to list all medications the patient is currently using.
        2. Use the tool `findConditionsByPatient` 'obtain clinical Conditions history' to list clinical Conditions.
        Based only on these facts, answer the question.
    """)
    String answerQuestion(@V("patientKey") String patientKey, @V("question") String question);


}
