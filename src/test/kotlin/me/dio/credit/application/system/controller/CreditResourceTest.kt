package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.impl.CreditService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@SpringBootTest
@ActiveProfiles("teste")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {

    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var creditService: CreditService

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setup() = creditRepository.deleteAll()

    @AfterEach
    fun tearDown() = creditRepository.deleteAll()

    @Test
    fun `should create a credit and return 201 status`() {
        //given
        val creditDto: CreditDto = builderCreditDto()
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL).contentType(MediaType.APPLICATION_JSON).content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(""))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dayFirstInstallment").value("LocalDate"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value("5"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value("1"))
    }

    @Test
    fun `should find a credit by customerId and return 200 status`() {
        //given
        val credit: Credit = creditRepository.save(builderCreditDto().toEntity())
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL?customerId=${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(""))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dayFirstInstallment").value("LocalDate"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value("5"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value("1"))
            .andDo(MockMvcResultHandlers.print())
    }


    @Test
    fun `should find a credit by creditCode and return 200 status`() {
        //given
        val fakeCreditCode: UUID = UUID.randomUUID()
        val fakeCredit: Credit = buildCredit(creditCode = fakeCreditCode)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("/credit/{creditCode}", fakeCreditCode.toString())
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(""))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dayFirstInstallment").value("LocalDate"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value("5"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value("1"))
            .andDo(MockMvcResultHandlers.print())
    }

    private fun builderCreditDto(
        creditValue: BigDecimal = BigDecimal.valueOf(1000.0),
        dayFirstInstallment: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 5,
        customerId: Long = 1L,
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )

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

    private fun builderCustomerDto(
        firstName: String = "Amanda",
        lastName: String = "Queiroz",
        cpf: String = "28475934625",
        email: String = "amanda@email.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "1234",
        zipCode: String = "000000",
        street: String = "Rua da Amanda, 123",
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )
}
