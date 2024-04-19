package ch.admin.foitt.openid4vc.utils

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.Curve

fun JWSAlgorithm.toCurve(): Curve = Curve.forJWSAlgorithm(this).first() // TODO: support return of multiple curves
