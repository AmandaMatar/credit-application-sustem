package me.dio.credit.application.system.service
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*


@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK lateinit var creditRepository: CreditRepository
    @MockK lateinit var customerRepository: CustomerRepository
    @InjectMockKs lateinit var creditService: CustomerService


    @Test
    fun `should save credit`() {
        //given
        val fakeCredit: Credit = buildCredit()
        every { creditRepository.save(any()) } returns fakeCredit
        //when
        val actual: Customer = creditService.save(fakeCredit)
        //then
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
        Assertions.assertThat(actual).isNotNull
    }

    @Test
    fun `should find all by Customer`(){
        //given
        val customerId: Long = 1L
        val fakeCredit: Credit = buildCredit()
        every { creditRepository.findAllByCustomerId(customerId) } returns listOf(fakeCredit)
        //when
        val actual = creditRepository.findAllByCustomerId(customerId)
        //then
        verify(exactly = 1) { creditRepository.findAllByCustomerId(customerId) }
        Assertions.assertThat(actual).isNotNull
    }

    @Test
    fun `should find by credit code`() {
        //given
        val fakeCreditCode: UUID = UUID.randomUUID()
        val fakeCredit: Credit = buildCredit(creditCode = fakeCreditCode)
        every { creditRepository.findByCreditCode(fakeCreditCode) }.returns(fakeCredit)
        //when
        val actual: Credit = creditService.findByCreditCode(fakeCreditCode)
        //then
        Assertions.assertThat(actual).isExactlyInstanceOf(Credit::class.java)
        Assertions.assertThat(actual).isNotNull
        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCreditCode) }
    }

    private fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        value: BigDecimal = BigDecimal.valueOf(1000.0),
        firstInstallmentDate: LocalDate = LocalDate.now().plusDays(30),
        numberOfInstallments: Int = 5
    ): Credit {
        return Credit(
            creditCode = creditCode,
            creditValue = value,
            dayFirstInstallment = firstInstallmentDate,
            numberOfInstallments = numberOfInstallments
        )
    }
}





