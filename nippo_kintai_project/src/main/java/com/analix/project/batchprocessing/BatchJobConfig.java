package com.analix.project.batchprocessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.analix.project.entity.Users;
import com.analix.project.service.AttendanceService;
import com.analix.project.service.DailyReportService;
import com.analix.project.service.EmailService;
import com.analix.project.service.InformationService;

@Configuration
public class BatchJobConfig {

	@Autowired
	private DailyReportService dailyReportService;

	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private InformationService informationService;

	@Autowired
	private EmailService emailService;
	@Autowired
	private PlatformTransactionManager transactionManager;

	// Jobの定義
	@Bean
	public Job dailyReportAndAttendanceJob(JobRepository jobRepository, Step collectUnsubmittedDailyReportUsersStep,
			Step collectUnsubmittedAttendanceUsersStep, Step sendInfoStep, Step sendEmailsStep) {
		return new JobBuilder("dailyReportAndAttendanceJob", jobRepository)
				.start(collectUnsubmittedDailyReportUsersStep)
				.next(collectUnsubmittedAttendanceUsersStep)
				.next(sendInfoStep)
				.next(sendEmailsStep)

				.build();
	}

	//	// 未提出の日報ユーザーを収集するStep
	@Bean
	public Step collectUnsubmittedDailyReportUsersStep(JobRepository jobRepository) {
		return new StepBuilder("collectUnsubmittedDailyReportUsersStep", jobRepository)
				.tasklet((contribution, chunkContext) -> {
					// 未提出日報ユーザーを取得
					List<Users> unsubmittedDailyReportUserList = dailyReportService.registCheck();
					System.out.println("unsubmittedDailyReportUserList" + unsubmittedDailyReportUserList);
					// ExecutionContextに保存
					chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
							.put("dailyReportUsers", unsubmittedDailyReportUserList);

					return RepeatStatus.FINISHED;
				}, transactionManager).build();
	}

	// 未提出の勤怠ユーザーを収集するStep
	@Bean
	public Step collectUnsubmittedAttendanceUsersStep(JobRepository jobRepository) {
		return new StepBuilder("collectUnsubmittedAttendanceUsersStep", jobRepository)
				.tasklet((contribution, chunkContext) -> {

					// 未提出勤怠ユーザーを取得
					List<Users> unsubmittedAttendanceUserList = attendanceService.registCheck();
					System.out.println(unsubmittedAttendanceUserList);

					//					
					//					Object obj = chunkContext.getStepContext().getStepExecution().getExecutionContext()
					//							.get("attendanceUsers");
					//
					//					if (obj instanceof List<?>) {
					//						unsubmittedAttendanceUserList = (List<Users>) obj;
					//
					//					} else {
					//						// エラーハンドリング: 型が一致しない場合の処理
					//						throw new IllegalStateException(
					//								"Expected a List<Users> but got: " + (obj != null ? obj.getClass().getName() : "null"));
					//					}
					// ExecutionContextに保存
					chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
							.put("attendanceUsers", unsubmittedAttendanceUserList);
					return RepeatStatus.FINISHED;
				}, transactionManager).build();
	}

	/**
	 * 日報勤怠忘れリストをお知らせテーブル登録メソッドへ渡す
	 * @param jobRepository
	 * @return
	 */
	@Bean
	public Step sendInfoStep(JobRepository jobRepository) {
		return new StepBuilder("sendInfoStep", jobRepository)
				.tasklet((contribution, chunkContext) -> {
					//					 // ExecutionContextから取得
					List<Users> unsubmittedDailyReportUserList = (List<Users>) chunkContext.getStepContext()
							.getStepExecution().getJobExecution()
							.getExecutionContext()
							.get("dailyReportUsers");
					List<Users> unsubmittedAttendanceUserList = (List<Users>) chunkContext.getStepContext()
							.getStepExecution().getJobExecution()
							.getExecutionContext().get("attendanceUsers");

					// nullチェック
					if (unsubmittedDailyReportUserList == null || unsubmittedAttendanceUserList == null) {
						throw new IllegalStateException("Daily Report Users list is null.");
					}
					System.out.println("Unsubmitted Daily Report Users: " + unsubmittedDailyReportUserList);
					System.out.println("Unsubmitted attendance Users: " + unsubmittedAttendanceUserList);
					informationService.insertNotificationsForBatch(unsubmittedDailyReportUserList,unsubmittedAttendanceUserList);

					
					//					System.out.println(attendanceUsers);
					return RepeatStatus.FINISHED;
				}, transactionManager).build();

	}

	// メール送信Step
	@Bean
	public Step sendEmailsStep(JobRepository jobRepository) {
		return new StepBuilder("sendEmailsStep", jobRepository)
				.tasklet((contribution, chunkContext) -> {
					List<Users> unsubmittedDailyReportUserList = (List<Users>) chunkContext.getStepContext()
							.getStepExecution().getJobExecution()
							.getExecutionContext()
							.get("dailyReportUsers");
					List<Users> unsubmittedAttendanceUserList = (List<Users>) chunkContext.getStepContext()
							.getStepExecution().getJobExecution()
							.getExecutionContext().get("attendanceUsers");

					// 未提出リストをマップにまとめてメール送信
					Map<String, List<Users>> umsubmitMap = new HashMap<>();
					umsubmitMap.put("dailyReport", unsubmittedDailyReportUserList);
					umsubmitMap.put("attendance", unsubmittedAttendanceUserList);

					// メールサービスでメール送信
					emailService.sendForgetRegistEmails(umsubmitMap);
					return RepeatStatus.FINISHED;
				}, transactionManager).build();
	}

	//	@Bean
	//	public Step sendEmailsStep(JobRepository jobRepository) {
	//		return new StepBuilder("sendEmailsStep", jobRepository)
	//				.tasklet((contribution, chunkContext) -> {
	//					//					Object dailyReportUsersObj = chunkContext.getStepContext().getStepExecution().getExecutionContext().get("dailyReportUsers");
	//					//					if (dailyReportUsersObj instanceof List<?>) {
	//					//					    List<Users> umsubmittedDailyReportUserList = (List<Users>) dailyReportUsersObj;
	//					//					}
	//					//					// ExecutionContextから未提出者リストを取得
	//					//					ExecutionContext stepContext = chunkContext.getStepContext().getStepExecution()
	//					//							.getExecutionContext();
	//					//					List<Users> umsubmittedDailyReportUserList = (List<Users>) stepContext.get("dailyReportUsers");
	//					//					List<Users> umsubmittedAttendanceUserList = (List<Users>) stepContext.get("attendanceUsers");
	//					//
	//					//					// 未提出リストをマップにまとめてメール送信
	//					//					Map<String, List<Users>> umsubmitMap = new HashMap<>();
	//					//					umsubmitMap.put("dailyReport", umsubmittedDailyReportUserList);
	//					//					umsubmitMap.put("attendance", umsubmittedAttendanceUserList);
	//
	//					// メールサービスでメール送信
	//					emailService.sendForgetRegistEmails();
	//					return RepeatStatus.FINISHED;
	//				}, transactionManager).build();
	//	}

}
