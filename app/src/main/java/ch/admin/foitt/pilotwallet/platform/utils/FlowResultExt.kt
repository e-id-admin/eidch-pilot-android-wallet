package ch.admin.foitt.pilotwallet.platform.utils

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

fun <V, E, F> Flow<Result<V, E>>.mapError(transform: suspend (E) -> F): Flow<Result<V, F>> =
    map {
        when {
            it.isErr -> Err(transform(it.error))
            else -> Ok(it.value)
        }
    }

fun <V, W, E> Flow<Result<V, E>>.mapOk(transform: suspend (V) -> W): Flow<Result<W, E>> =
    map {
        when {
            it.isOk -> Ok(transform(it.value))
            else -> Err(it.error)
        }
    }

fun <V, E, U> Flow<Result<V, E>>.andThen(transform: suspend (V) -> Result<U, E>): Flow<Result<U, E>> =
    map {
        when {
            it.isOk -> transform(it.value)
            else -> Err(it.error)
        }
    }

fun <V, F> Flow<V>.catchAndMap(transform: suspend (Throwable) -> F): Flow<Result<V, F>> =
    flow {
        catch { throwable ->
            emit(Err(transform(throwable)))
        }.collect {
            emit(Ok(it))
        }
    }
