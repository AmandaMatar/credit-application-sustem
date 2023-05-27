package me.dio.credit.application.system.service

import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import java.util.*

interface ICustomerService {
  fun save(customer: Credit): Customer
  fun findById(id: Long): Customer
  fun delete(id: Long)
  fun findByCreditCode(fakeCreditCode: UUID): Credit
}