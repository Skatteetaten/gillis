package no.skatteetaten.aurora.gorg.controller

import no.skatteetaten.aurora.gorg.service.CrawlService
import no.skatteetaten.aurora.gorg.service.DeleteService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/projects")
class ProjectController(val crawler: CrawlService, val deletionService: DeleteService) {

    @DeleteMapping
    fun deleteProjects() {
        crawler.findTemporaryProjects(Instant.now())
                .filter { it.ttl.isNegative }
                .forEach { deletionService.deleteProject(it) }

    }

    @GetMapping
    fun list(): List<CrawlService.TemporaryProject> {
        return crawler.findTemporaryProjects(Instant.now())
    }

}
