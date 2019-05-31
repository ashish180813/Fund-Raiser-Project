package com.moneydonationpool.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moneydonationpool.dao.CauseDao;
import com.moneydonationpool.dao.DonationDao;
import com.moneydonationpool.entity.CauseEntity;
import com.moneydonationpool.entity.DonationEntity;
import com.moneydonationpool.exception.MoneyDonationPoolException;
import com.moneydonationpool.service.CauseService;
import com.moneydonationpool.service.DonationService;

@Service
@Transactional
public class DonationServiceImpl implements DonationService {

	@Autowired
	DonationDao donationDao;

	@Autowired
	CauseService causeService;
	
	@Autowired
	CauseDao causeDao;

	@Override
	public DonationEntity postDonationDetails(DonationEntity donationDetails) throws MoneyDonationPoolException {
		int causeId = donationDetails.getCauseId();
		DonationEntity donationEntity = null;
		CauseEntity causeDetails = causeService.getCauseById(causeId);
		if(causeDetails.getCauseTargetAmt() == donationDetails.getAmountDonated() + causeDetails.getCollectedAmt())
		{
			donationEntity = donationDao.postDonationDetails(donationDetails);
			causeDetails.setCollectedAmt(donationDetails.getAmountDonated() + causeDetails.getCollectedAmt());
			causeDetails.setActive(false);
			causeDao.updateCause(causeDetails);
			return donationEntity;
		}
		else if(causeDetails.getCauseTargetAmt() < donationDetails.getAmountDonated() + causeDetails.getCollectedAmt()) {
			throw new MoneyDonationPoolException(com.moneydonationpool.exception.ErrorCodes.TARGET_AMOUNT_EXCEEDS);
		}
		donationEntity = donationDao.postDonationDetails(donationDetails);
		causeDetails.setCollectedAmt(donationEntity.getAmountDonated()+causeDetails.getCollectedAmt());
		causeDao.updateCause(causeDetails);
		return donationEntity;
	}

}
