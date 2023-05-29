package me.dio.credit.application.system.service.impl

import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.ICustomerService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomerService(
  private val customerRepository: CustomerRepository
): ICustomerService {
  fun save(customer: Customer): Customer = this.customerRepository.save(customer)
  override fun save(customer: Credit): Customer {
    TODO("Not yet implemented")
  }

  override fun findById(id: Long): Customer = this.customerRepository.findById(id)
    .orElseThrow{throw BusinessException("Id $id not found") }

  override fun delete(id: Long) {
    val customer: Customer = this.findById(id)
    this.customerRepository.delete(customer)
  }

  override fun findByCreditCode(fakeCreditCode: UUID): Credit {
    TODO("Not yet implemented")
  }
}