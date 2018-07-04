package com.zimidy.api.layers.storage.entities.sociallogin

import com.zimidy.api.App
import com.zimidy.api.AppProperties
import com.zimidy.api.configurations.security.JwtHelper
import com.zimidy.api.configurations.security.UserPointsService
import com.zimidy.api.layers.api.graphql.resolvers.nodes.UserNode
import com.zimidy.api.layers.enumClass.AccountStatus
import com.zimidy.api.layers.storage.entities.node.nodes.User
import com.zimidy.api.layers.storage.repositories.node.nodes.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.MacProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ResolvableType
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.client.RestTemplate
import java.security.Key
import java.util.Date
import java.util.HashMap
import java.util.concurrent.TimeUnit
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@Controller
class LoginController(
    private val userRepository: UserRepository,
    private val userPointsService: UserPointsService,
    private val app: AppProperties
) {
    internal var oauth2AuthenticationUrls: MutableMap<String, String> = HashMap()

    @Autowired
    private val authorizedClientService: OAuth2AuthorizedClientService? = null

    @Autowired
    private val clientRegistrationRepository: ClientRegistrationRepository? = null

    @GetMapping("/oauth_login")
    fun getLoginPage(model: Model): String {
        var clientRegistrations: Iterable<ClientRegistration>? = null
        val type = ResolvableType.forInstance(clientRegistrationRepository!!)
            .`as`(Iterable::class.java)
        if (type !== ResolvableType.NONE && ClientRegistration::class.java.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = clientRegistrationRepository as Iterable<ClientRegistration>?
        }
        clientRegistrations!!.forEach { n ->
            oauth2AuthenticationUrls[n.clientName] = authorizationRequestBaseUri + "/" + n.registrationId
        }
        model.addAttribute("urls", oauth2AuthenticationUrls)
        return "oauth_login"
    }

    @GetMapping("/loginSuccess")
    fun getLoginInfoSucess(
        model: Model,
        authentication: OAuth2AuthenticationToken,
        httpresponse: HttpServletResponse
    ): String {
        println("Inside Success")
        val client = authorizedClientService!!
            .loadAuthorizedClient<OAuth2AuthorizedClient>(
                authentication.authorizedClientRegistrationId,
                authentication.name
            )
        val userInfoEndpointUri = client.clientRegistration
            .providerDetails.userInfoEndpoint.uri
        if (userInfoEndpointUri != null || userInfoEndpointUri !== "") {
            val restTemplate = RestTemplate()
            val headers = HttpHeaders()
            println("client name  -- - > " + client.clientRegistration.clientName)
            println("client.getAccessToken()- > " + client.accessToken.tokenValue)
            headers.add(
                HttpHeaders.AUTHORIZATION, "Bearer " + client.accessToken
                    .tokenValue
            )
            val entity = HttpEntity("", headers)
            val response = restTemplate
                .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map::class.java)
            val userAttributes = response.body
            println("email - > " + userAttributes!!["email"])
            var email = userAttributes!!["email"]
            var user: User? = null
            println("userAttributes.get(\"name\") - > " + userAttributes.toString())

            user = if (email == "" || email == null) {
                println("uin IF " + user.toString())
                null
            } else {
                println("in else - --  " + user.toString())
                userRepository.findByEmail(email as String)
            }
            println("user obj from DB - > " + user.toString())

            if (user == null) {
                val user: User = User()
                user.email = email as String
                user.firstName = userAttributes!!["given_name"] as String
                user.lastName = userAttributes!!["family_name"] as String
                user.defaultImageFileName = userAttributes!!["picture"] as String
                val genderdata = userAttributes!!["gender"] as String
                if (genderdata.equals("male")) {
                    user.gender = User.Gender.MALE
                } else if (genderdata.equals("female")) {
                    user.gender = User.Gender.FEMALE
                } else {
                    user.gender = User.Gender.PREFERNOTTOANSWER
                }
                user.accountStatus = AccountStatus.PENDING
                println("user is null so user saving  " + user.toString())
                userPointsService.addSignUpPoints(user!!)
                user.roles = mutableListOf("ROLE_USER")
                println("~~~~~~~~~gonna save object ---~~~~" + user.toString().toString())
                userRepository.save(user!!)
                val compactSerializedClaimsJws = Jwts.builder()
                    .setSubject(user.getIdOrThrow())
                    .setExpiration(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))
                    .claim(Claim.ROLES.name, user.roles)
                    .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                    .compact()

                val tokenCookie = Cookie(JwtHelper.TOKEN_COOKIE_NAME, compactSerializedClaimsJws)
                tokenCookie.path = "/"
                tokenCookie.isHttpOnly = true
                tokenCookie.secure = app.env == AppProperties.PROD
                httpresponse.addCookie(tokenCookie)
                httpresponse.status = 200
                println("redirect:${app.web.protocol}://${app.web.domain}:${app.web.port}/browseEvents")
                httpresponse.setHeader("user", App.JSON.writeValueAsString(UserNode(user)))
                httpresponse.sendRedirect("${app.web.protocol}://${app.web.domain}:${app.web.port}/browseEvents")
            }
            if (user != null) {
                println("user is null so user saving  " + user.toString())
                val compactSerializedClaimsJws = Jwts.builder()
                    .setSubject(user.getIdOrThrow())
                    .setExpiration(Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)))
                    .claim(Claim.ROLES.name, user.roles)
                    .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                    .compact()

                val tokenCookie = Cookie(JwtHelper.TOKEN_COOKIE_NAME, compactSerializedClaimsJws)
                tokenCookie.path = "/"
                tokenCookie.isHttpOnly = true
                tokenCookie.secure = app.env == AppProperties.PROD
                httpresponse.addCookie(tokenCookie)
                httpresponse.setHeader("user", App.JSON.writeValueAsString(UserNode(user)))
                httpresponse.sendRedirect("${app.web.protocol}://${app.web.domain}:${app.web.port}/browseEvents")
            }
        }
        return "loginSuccess"
    }

    @GetMapping("/loginFailure")
    fun loginFailure(
        model: Model,
        authentication: OAuth2AuthenticationToken,
        httpresponse: HttpServletResponse
    ): String {
        println("hai login Failed")
        return "loginError"
    }

    companion object {
        internal const val TOKEN_COOKIE_NAME = "token"
        private val SECRET_KEY: Key = MacProvider.generateKey()
        private val authorizationRequestBaseUri = "oauth2/authorization"
    }

    private enum class Claim {
        ROLES
    }
}
