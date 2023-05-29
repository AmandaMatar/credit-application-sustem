package me.dio.credit.application.system.service
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.impl.CreditService
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*


@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK lateinit var creditRepository: CreditRepository
    @MockK lateinit var customerService: CustomerService
    @InjectMockKs lateinit var creditService: CreditService

    @Test
    fun `should save credit`() {
        //given
        val fakeCredit: Credit = buildCredit()
        val fakeCustomer: Customer = buildCustomer()

        every { customerService.findById(any()) } returns fakeCustomer
        every { creditRepository.save(any()) } returns fakeCredit

        //when
        val actual: Credit = creditService.save(fakeCredit)
        //then
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
        verify(exactly = 1) { customerService.findById(any()) }
        Assertions.assertThat(actual).isNotNull
    }
    @Test
    fun `should not save a credit if day of installments is after 3 months`() {
        val fakeCredit: Credit = buildCredit(firstInstallmentDate = LocalDate.now().plusMonths(4))
        assertThrows<BusinessException> {
            creditService.save(fakeCredit)
        }
    }

    @Test
    fun `should find all credits by customer id`(){
        //given
        val customerId: Long = 1L
        val fakeCredit: Credit = buildCredit()
        every { creditRepository.findAllByCustomerId(any()) } returns listOf(fakeCredit)
        //when
        val actual = creditService.findAllByCustomer(customerId)
        //then
        verify(exactly = 1) { creditRepository.findAllByCustomerId(customerId) }
        Assertions.assertThat(actual.size).isEqualTo(1)
    }

    @Test
    fun `should find by credit code and customer id`() {
        //given
        val fakeCreditCode: UUID = UUID.randomUUID()
        val fakeCustomerId = 1L
        val fakeCredit: Credit = buildCredit(creditCode = fakeCreditCode)

        every { creditRepository.findByCreditCode(any()) }.returns(fakeCredit)

        //when
        val actual: Credit = creditService.findByCreditCodeAndCustomerId(fakeCustomerId, fakeCreditCode)

        //then
        Assertions.assertThat(actual).isNotNull
        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCreditCode) }
    }

    @Test
    fun `should not find by credit code and customer id when credit not found`() {
        //given
        val fakeCreditCode: UUID = UUID.randomUUID()
        val fakeCustomerId = 1L

        every { creditRepository.findByCreditCode(any()) } returns null

        //when

        assertThrows<BusinessException> {
            creditService.findByCreditCodeAndCustomerId(fakeCustomerId, fakeCreditCode)
        }

        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCreditCode) }
    }

    @Test
    fun `should not find credit when customer id is invalid`() {
        //given
        val fakeCreditCode: UUID = UUID.randomUUID()
        val fakeCustomerId = 1L
        val fakeCredit: Credit = buildCredit(customer = buildCustomer(id = 2L))


        every { creditRepository.findByCreditCode(any()) }.returns(fakeCredit)

        //when
        assertThrows<IllegalArgumentException> {
            creditService.findByCreditCodeAndCustomerId(fakeCustomerId, fakeCreditCode)
        }

        verify(exactly = 1) { creditRepository.findByCreditCode(fakeCreditCode) }
    }



    private fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        value: BigDecimal = BigDecimal.valueOf(1000.0),
        firstInstallmentDate: LocalDate = LocalDate.now().plusDays(30),
        numberOfInstallments: Int = 5,
        customer: Customer = buildCustomer()
    ): Credit {
        return Credit(
            creditCode = creditCode,
            creditValue = value,
            dayFirstInstallment = firstInstallmentDate,
            numberOfInstallments = numberOfInstallments,
            customer = customer
        )
    }

    private fun buildCustomer(
        firstName: String = "Amanda",
        lastName: String = "Queiroz",
        cpf: String = "28475934625",
        email: String = "amanda@gmail.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua da Amanda",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        id: Long = 1L
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street,
        ),
        income = income,
        id = id
    )
}





