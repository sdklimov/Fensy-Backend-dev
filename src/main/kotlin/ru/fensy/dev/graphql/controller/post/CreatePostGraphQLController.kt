package ru.fensy.dev.graphql.controller.post

import org.springframework.stereotype.Controller
import ru.fensy.dev.usecase.post.CreatePostUseCase

@Controller
class CreatePostGraphQLController(
    private val createPostUseCase: CreatePostUseCase,

    ) {

//    @MutationMapping("createPost")
//    suspend fun createPost(@Argument input: CreatePostInput): Post {
////        val createdPostId = createPostUseCase.execute(input)
////        return createdPostId
//        return Post("1")
//    }

}